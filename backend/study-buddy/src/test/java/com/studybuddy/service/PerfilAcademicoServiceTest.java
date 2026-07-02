package com.studybuddy.service;

import com.studybuddy.dto.request.AtualizarPerfilAcademicoRequestDTO;
import com.studybuddy.dto.response.PerfilAcademicoResponseDTO;
import com.studybuddy.exception.EstudanteNaoEncontradoException;
import com.studybuddy.mapper.PerfilAcademicoMapper;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.NivelConhecimento;
import com.studybuddy.model.enums.ObjetivoEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import com.studybuddy.repository.EstudanteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilAcademicoServiceTest {

    @Mock
    private EstudanteRepository estudanteRepository;

    private PerfilAcademicoService service;
    private UUID estudanteId;
    private Estudante estudante;
    private AtualizarPerfilAcademicoRequestDTO atualizarDTO;

    @BeforeEach
    void setUp() {
        service = new PerfilAcademicoService(estudanteRepository, new PerfilAcademicoMapper());
        estudanteId = UUID.randomUUID();
        estudante = new Estudante();
        estudante.setId(estudanteId);
        estudante.setNome("Ana Silva");
        estudante.setEmail("ana@email.com");
        estudante.setTelefone("85999999999");
        estudante.setAtivo(true);
        estudante.setMatricula("20260001");
        estudante.setInstituicao("Universidade Federal");

        atualizarDTO = new AtualizarPerfilAcademicoRequestDTO(
                "Computacao",
                Set.of("Algoritmos", "Estrutura de Dados"),
                Set.of(PeriodoDisponibilidade.NOITE, PeriodoDisponibilidade.FIM_DE_SEMANA),
                ObjetivoEstudo.PROVA,
                NivelConhecimento.INTERMEDIARIO,
                ModalidadeEstudo.ONLINE,
                "Procuro grupo focado em exercicios"
        );
    }

    @Test
    void deveBuscarPerfilAcademicoExistente() {
        estudante.setPerfilAcademico(perfilAcademico(atualizarDTO));
        when(estudanteRepository.findById(estudanteId)).thenReturn(Optional.of(estudante));

        PerfilAcademicoResponseDTO response = service.buscarPerfil(estudanteId);

        assertEquals(estudanteId, response.estudanteId());
        assertEquals("Computacao", response.curso());
        assertEquals(atualizarDTO.objetivoEstudo(), response.objetivoEstudo());
        assertEquals(atualizarDTO.nivelConhecimento(), response.nivelConhecimento());
    }

    @Test
    void deveCriarPerfilAcademicoQuandoAusente() {
        when(estudanteRepository.findById(estudanteId)).thenReturn(Optional.of(estudante));
        when(estudanteRepository.save(estudante)).thenReturn(estudante);

        PerfilAcademicoResponseDTO response = service.atualizarPerfil(estudanteId, atualizarDTO);

        assertNotNull(estudante.getPerfilAcademico());
        assertEquals("Computacao", response.curso());
        assertEquals(atualizarDTO.disciplinasInteresse(), response.disciplinasInteresse());
        assertEquals(atualizarDTO.disponibilidade(), response.disponibilidade());
        assertEquals(atualizarDTO.modalidadePreferida(), response.modalidadePreferida());
        verify(estudanteRepository).save(estudante);
    }

    @Test
    void deveAtualizarPerfilAcademicoExistente() {
        PerfilAcademico perfil = perfilAcademico(new AtualizarPerfilAcademicoRequestDTO(
                "Matematica",
                Set.of("Calculo"),
                Set.of(PeriodoDisponibilidade.MANHA),
                ObjetivoEstudo.REFORCO,
                NivelConhecimento.INICIANTE,
                ModalidadeEstudo.PRESENCIAL,
                "Perfil antigo"
        ));
        estudante.setPerfilAcademico(perfil);
        when(estudanteRepository.findById(estudanteId)).thenReturn(Optional.of(estudante));
        when(estudanteRepository.save(estudante)).thenReturn(estudante);

        PerfilAcademicoResponseDTO response = service.atualizarPerfil(estudanteId, atualizarDTO);

        assertEquals(perfil, estudante.getPerfilAcademico());
        assertEquals("Computacao", response.curso());
        assertEquals(atualizarDTO.disciplinasInteresse(), response.disciplinasInteresse());
        assertEquals(atualizarDTO.descricao(), response.descricao());
        verify(estudanteRepository).save(estudante);
    }

    @Test
    void deveFalharAoBuscarPerfilDeEstudanteInexistente() {
        when(estudanteRepository.findById(estudanteId)).thenReturn(Optional.empty());

        assertThrows(EstudanteNaoEncontradoException.class, () -> service.buscarPerfil(estudanteId));
    }

    @Test
    void naoDeveAtualizarPerfilDeEstudanteInexistente() {
        when(estudanteRepository.findById(estudanteId)).thenReturn(Optional.empty());

        assertThrows(EstudanteNaoEncontradoException.class,
                () -> service.atualizarPerfil(estudanteId, atualizarDTO));
        verify(estudanteRepository, never()).save(estudante);
    }

    private PerfilAcademico perfilAcademico(AtualizarPerfilAcademicoRequestDTO dto) {
        PerfilAcademico perfil = new PerfilAcademico();
        perfil.setId(UUID.randomUUID());
        perfil.setCurso(dto.curso());
        perfil.setDisciplinasInteresse(new LinkedHashSet<>(dto.disciplinasInteresse()));
        perfil.setDisponibilidade(new LinkedHashSet<>(dto.disponibilidade()));
        perfil.setObjetivoEstudo(dto.objetivoEstudo());
        perfil.setNivelConhecimento(dto.nivelConhecimento());
        perfil.setModalidadePreferida(dto.modalidadePreferida());
        perfil.setDescricao(dto.descricao());
        return perfil;
    }
}
