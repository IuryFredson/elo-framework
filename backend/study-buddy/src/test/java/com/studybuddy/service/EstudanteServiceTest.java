package com.studybuddy.service;

import com.studybuddy.dto.request.AtualizarEstudanteRequestDTO;
import com.studybuddy.dto.request.CriarEstudanteRequestDTO;
import com.studybuddy.dto.response.EstudanteResponseDTO;
import com.studybuddy.exception.EmailJaCadastradoException;
import com.studybuddy.exception.EstudanteNaoEncontradoException;
import com.studybuddy.exception.MatriculaJaCadastradaException;
import com.studybuddy.mapper.EstudanteMapper;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.repository.EstudanteRepository;
import com.studybuddy.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudanteServiceTest {

    @Mock
    private EstudanteRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private EstudanteService service;
    private CriarEstudanteRequestDTO criarDTO;

    @BeforeEach
    void setUp() {
        service = new EstudanteService(repository, usuarioRepository, new EstudanteMapper());
        criarDTO = new CriarEstudanteRequestDTO(
                "Ana Silva",
                "ana@email.com",
                "85999999999",
                "20260001",
                "Universidade Federal"
        );
    }

    @Test
    void deveCriarEstudanteComEmailEMatriculaDisponiveis() {
        Estudante estudanteSalvo = estudanteSalvo(criarDTO);
        when(usuarioRepository.existsByEmail(criarDTO.email())).thenReturn(false);
        when(repository.existsByMatricula(criarDTO.matricula())).thenReturn(false);
        when(repository.save(any(Estudante.class))).thenReturn(estudanteSalvo);

        EstudanteResponseDTO response = service.criar(criarDTO);

        assertEquals(estudanteSalvo.getId(), response.id());
        assertEquals(criarDTO.nome(), response.nome());
        assertEquals(criarDTO.email(), response.email());
        assertEquals(criarDTO.matricula(), response.matricula());
        assertEquals(criarDTO.instituicao(), response.instituicao());
        assertTrue(response.ativo());
        verify(repository).save(any(Estudante.class));
    }

    @Test
    void naoDeveCriarEstudanteComEmailJaUsadoPorQualquerUsuario() {
        when(usuarioRepository.existsByEmail(criarDTO.email())).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> service.criar(criarDTO));

        verify(repository, never()).existsByMatricula(criarDTO.matricula());
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveCriarEstudanteComMatriculaJaCadastrada() {
        when(usuarioRepository.existsByEmail(criarDTO.email())).thenReturn(false);
        when(repository.existsByMatricula(criarDTO.matricula())).thenReturn(true);

        assertThrows(MatriculaJaCadastradaException.class, () -> service.criar(criarDTO));

        verify(repository, never()).save(any());
    }

    @Test
    void deveListarTodosOsEstudantes() {
        Estudante primeiro = estudanteSalvo(criarDTO);
        Estudante segundo = estudanteSalvo(new CriarEstudanteRequestDTO(
                "Bruno Costa",
                "bruno@email.com",
                "85988888888",
                "20260002",
                "Universidade Federal"
        ));
        when(repository.findAll()).thenReturn(List.of(primeiro, segundo));

        List<EstudanteResponseDTO> response = service.listarTodos();

        assertEquals(2, response.size());
        assertEquals(primeiro.getEmail(), response.get(0).email());
        assertEquals(segundo.getEmail(), response.get(1).email());
    }

    @Test
    void deveBuscarEstudantePorId() {
        Estudante estudante = estudanteSalvo(criarDTO);
        when(repository.findById(estudante.getId())).thenReturn(Optional.of(estudante));

        EstudanteResponseDTO response = service.buscarPorId(estudante.getId());

        assertEquals(estudante.getId(), response.id());
        assertEquals(estudante.getMatricula(), response.matricula());
    }

    @Test
    void deveFalharAoBuscarEstudanteInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EstudanteNaoEncontradoException.class, () -> service.buscarPorId(id));
    }

    @Test
    void deveAtualizarEstudante() {
        Estudante estudante = estudanteSalvo(criarDTO);
        AtualizarEstudanteRequestDTO atualizarDTO = new AtualizarEstudanteRequestDTO(
                "Ana Souza",
                "ana.souza@email.com",
                "85977777777",
                "20260003",
                "Instituto Federal"
        );
        when(repository.findById(estudante.getId())).thenReturn(Optional.of(estudante));
        when(usuarioRepository.existsByEmail(atualizarDTO.email())).thenReturn(false);
        when(repository.existsByMatricula(atualizarDTO.matricula())).thenReturn(false);
        when(repository.save(estudante)).thenReturn(estudante);

        EstudanteResponseDTO response = service.atualizar(estudante.getId(), atualizarDTO);

        assertEquals(atualizarDTO.nome(), response.nome());
        assertEquals(atualizarDTO.email(), response.email());
        assertEquals(atualizarDTO.matricula(), response.matricula());
        assertEquals(atualizarDTO.instituicao(), response.instituicao());
    }

    @Test
    void naoDeveAtualizarParaMatriculaJaCadastradaEmOutroEstudante() {
        Estudante estudante = estudanteSalvo(criarDTO);
        AtualizarEstudanteRequestDTO atualizarDTO = new AtualizarEstudanteRequestDTO(
                "Ana Souza",
                criarDTO.email(),
                criarDTO.telefone(),
                "20260003",
                criarDTO.instituicao()
        );
        when(repository.findById(estudante.getId())).thenReturn(Optional.of(estudante));
        when(repository.existsByMatricula(atualizarDTO.matricula())).thenReturn(true);

        assertThrows(MatriculaJaCadastradaException.class,
                () -> service.atualizar(estudante.getId(), atualizarDTO));
        verify(repository, never()).save(any());
    }

    @Test
    void deveAlterarStatusDoEstudante() {
        Estudante estudante = estudanteSalvo(criarDTO);
        when(repository.findById(estudante.getId())).thenReturn(Optional.of(estudante));
        when(repository.save(estudante)).thenReturn(estudante);

        EstudanteResponseDTO response = service.alterarStatus(estudante.getId(), false);

        assertFalse(response.ativo());
        verify(repository).save(estudante);
    }

    @Test
    void deveDeletarEstudante() {
        Estudante estudante = estudanteSalvo(criarDTO);
        when(repository.findById(estudante.getId())).thenReturn(Optional.of(estudante));

        service.deletar(estudante.getId());

        verify(repository).delete(estudante);
    }

    private Estudante estudanteSalvo(CriarEstudanteRequestDTO dto) {
        Estudante estudante = new Estudante();
        estudante.setId(UUID.randomUUID());
        estudante.setNome(dto.nome());
        estudante.setEmail(dto.email());
        estudante.setTelefone(dto.telefone());
        estudante.setAtivo(true);
        estudante.setMatricula(dto.matricula());
        estudante.setInstituicao(dto.instituicao());
        return estudante;
    }
}
