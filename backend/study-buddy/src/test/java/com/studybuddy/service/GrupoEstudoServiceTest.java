package com.studybuddy.service;

import com.studybuddy.dto.request.AtualizarGrupoEstudoRequestDTO;
import com.studybuddy.dto.request.CriarGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.GrupoEstudoResponseDTO;
import com.studybuddy.exception.AcessoNegadoException;
import com.studybuddy.exception.EstudanteInativoException;
import com.studybuddy.exception.EstudanteNaoEncontradoException;
import com.studybuddy.exception.GrupoEstudoNaoEncontradoException;
import com.studybuddy.mapper.GrupoEstudoMapper;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import com.studybuddy.model.enums.StatusGrupoEstudo;
import com.studybuddy.repository.EstudanteRepository;
import com.studybuddy.repository.GrupoEstudoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrupoEstudoServiceTest {

    @Mock
    private GrupoEstudoRepository grupoRepository;

    @Mock
    private EstudanteRepository estudanteRepository;

    private GrupoEstudoService service;
    private Estudante publicador;
    private CriarGrupoEstudoRequestDTO criarDTO;

    @BeforeEach
    void setUp() {
        service = new GrupoEstudoService(grupoRepository, estudanteRepository, new GrupoEstudoMapper());
        publicador = estudante("Ana Silva", true);
        criarDTO = new CriarGrupoEstudoRequestDTO(
                publicador.getId(),
                "Grupo de Algoritmos",
                "Encontros para resolver listas de algoritmos",
                "Algoritmos",
                5,
                ModalidadeEstudo.ONLINE,
                PeriodoDisponibilidade.NOITE
        );
    }

    @Test
    void deveCriarGrupoDeEstudoComPublicadorAtivo() {
        when(estudanteRepository.findById(publicador.getId())).thenReturn(Optional.of(publicador));
        when(grupoRepository.save(any(GrupoEstudo.class))).thenAnswer(invocation -> {
            GrupoEstudo grupo = invocation.getArgument(0);
            grupo.setId(UUID.randomUUID());
            return grupo;
        });

        GrupoEstudoResponseDTO response = service.criar(criarDTO);

        assertNotNull(response.id());
        assertEquals(criarDTO.titulo(), response.titulo());
        assertEquals(criarDTO.disciplina(), response.disciplina());
        assertEquals(publicador.getId(), response.publicadorId());
        assertEquals(StatusGrupoEstudo.ATIVO, response.status());
        assertNotNull(response.dataPublicacao());
        verify(grupoRepository).save(any(GrupoEstudo.class));
    }

    @Test
    void naoDeveCriarGrupoComPublicadorInexistente() {
        when(estudanteRepository.findById(publicador.getId())).thenReturn(Optional.empty());

        assertThrows(EstudanteNaoEncontradoException.class, () -> service.criar(criarDTO));
        verify(grupoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarGrupoComPublicadorInativo() {
        publicador.setAtivo(false);
        when(estudanteRepository.findById(publicador.getId())).thenReturn(Optional.of(publicador));

        assertThrows(EstudanteInativoException.class, () -> service.criar(criarDTO));
        verify(grupoRepository, never()).save(any());
    }

    @Test
    void deveListarTodosOsGrupos() {
        GrupoEstudo primeiro = grupo(publicador);
        GrupoEstudo segundo = grupo(estudante("Bruno Costa", true));
        segundo.setTitulo("Grupo de Banco de Dados");
        when(grupoRepository.findAll()).thenReturn(List.of(primeiro, segundo));

        List<GrupoEstudoResponseDTO> response = service.listarTodos();

        assertEquals(2, response.size());
        assertEquals(primeiro.getTitulo(), response.get(0).titulo());
        assertEquals(segundo.getTitulo(), response.get(1).titulo());
    }

    @Test
    void deveBuscarGrupoPorId() {
        GrupoEstudo grupo = grupo(publicador);
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));

        GrupoEstudoResponseDTO response = service.buscarPorId(grupo.getId());

        assertEquals(grupo.getId(), response.id());
        assertEquals(grupo.getPublicadorId(), response.publicadorId());
    }

    @Test
    void deveFalharAoBuscarGrupoInexistente() {
        UUID id = UUID.randomUUID();
        when(grupoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(GrupoEstudoNaoEncontradoException.class, () -> service.buscarPorId(id));
    }

    @Test
    void deveAtualizarGrupoQuandoSolicitanteForPublicador() {
        GrupoEstudo grupo = grupo(publicador);
        AtualizarGrupoEstudoRequestDTO atualizarDTO = new AtualizarGrupoEstudoRequestDTO(
                "Grupo de Estrutura de Dados",
                "Estudo de arvores e grafos",
                "Estrutura de Dados",
                4,
                ModalidadeEstudo.HIBRIDO,
                PeriodoDisponibilidade.FIM_DE_SEMANA
        );
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(grupoRepository.save(grupo)).thenReturn(grupo);

        GrupoEstudoResponseDTO response = service.atualizar(grupo.getId(), publicador.getId(), atualizarDTO);

        assertEquals(atualizarDTO.titulo(), response.titulo());
        assertEquals(atualizarDTO.disciplina(), response.disciplina());
        assertEquals(atualizarDTO.quantidadeVagas(), response.quantidadeVagas());
        assertEquals(atualizarDTO.modalidade(), response.modalidade());
        verify(grupoRepository).save(grupo);
    }

    @Test
    void naoDeveAtualizarGrupoDeOutroPublicador() {
        GrupoEstudo grupo = grupo(publicador);
        AtualizarGrupoEstudoRequestDTO atualizarDTO = new AtualizarGrupoEstudoRequestDTO(
                "Novo titulo",
                "Nova descricao",
                "Calculo",
                3,
                ModalidadeEstudo.PRESENCIAL,
                PeriodoDisponibilidade.MANHA
        );
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));

        assertThrows(AcessoNegadoException.class,
                () -> service.atualizar(grupo.getId(), UUID.randomUUID(), atualizarDTO));
        verify(grupoRepository, never()).save(any());
    }

    @Test
    void deveAtualizarStatusDoGrupo() {
        GrupoEstudo grupo = grupo(publicador);
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(grupoRepository.save(grupo)).thenReturn(grupo);

        GrupoEstudoResponseDTO response = service.atualizarStatus(grupo.getId(), StatusGrupoEstudo.PAUSADO);

        assertEquals(StatusGrupoEstudo.PAUSADO, response.status());
        verify(grupoRepository).save(grupo);
    }

    @Test
    void deveDeletarGrupoFisicamente() {
        GrupoEstudo grupo = grupo(publicador);
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));

        service.deletar(grupo.getId());

        verify(grupoRepository).delete(grupo);
        verify(grupoRepository, never()).save(any());
    }

    private Estudante estudante(String nome, boolean ativo) {
        Estudante estudante = new Estudante();
        estudante.setId(UUID.randomUUID());
        estudante.setNome(nome);
        estudante.setEmail(nome.toLowerCase().replace(" ", ".") + "@email.com");
        estudante.setTelefone("85999999999");
        estudante.setAtivo(ativo);
        estudante.setMatricula(UUID.randomUUID().toString());
        estudante.setInstituicao("Universidade Federal");
        return estudante;
    }

    private GrupoEstudo grupo(Estudante publicador) {
        GrupoEstudo grupo = new GrupoEstudo();
        grupo.setId(UUID.randomUUID());
        grupo.setTitulo("Grupo de Algoritmos");
        grupo.setDescricao("Encontros para resolver listas de algoritmos");
        grupo.setDisciplina("Algoritmos");
        grupo.setQuantidadeVagas(5);
        grupo.setModalidade(ModalidadeEstudo.ONLINE);
        grupo.setPeriodo(PeriodoDisponibilidade.NOITE);
        grupo.setStatus(StatusGrupoEstudo.ATIVO);
        grupo.setDataPublicacao(LocalDate.now());
        grupo.setPublicador(publicador);
        return grupo;
    }
}
