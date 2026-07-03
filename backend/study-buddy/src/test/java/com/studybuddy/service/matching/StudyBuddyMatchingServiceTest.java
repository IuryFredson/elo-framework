package com.studybuddy.service.matching;

import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.elo.compatibilidade.OrigemCompatibilidade;
import com.studybuddy.dto.response.StudyBuddyMatchingResponseDTO;
import com.studybuddy.exception.PerfilAcademicoAusenteException;
import com.studybuddy.mapper.StudyBuddyMatchingMapper;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class StudyBuddyMatchingServiceTest {

    @Mock
    private EstudanteRepository estudanteRepository;

    @Mock
    private CompatibilidadeAcademicaCalculator compatibilidadeCalculator;

    @Mock
    private StudyBuddyCompatibilidadeLlmProvider compatibilidadeLlmProvider;

    private StudyBuddyMatchingService service;
    private Estudante solicitante;
    private Estudante candidatoAlta;
    private Estudante candidatoBaixa;

    @BeforeEach
    void setUp() {
        service = new StudyBuddyMatchingService(
                estudanteRepository,
                compatibilidadeCalculator,
                compatibilidadeLlmProvider,
                new StudyBuddyMatchingMapper());
        solicitante = estudante("Ana Silva", perfil("Algoritmos"));
        candidatoAlta = estudante("Bruno Costa", perfil("Algoritmos"));
        candidatoBaixa = estudante("Carla Lima", perfil("Calculo"));
    }

    @Test
    void deveOrdenarMatchesPorCompatibilidadeELimitarPorTopN() {
        when(estudanteRepository.findById(solicitante.getId())).thenReturn(Optional.of(solicitante));
        when(estudanteRepository.buscarCandidatosMatching(solicitante.getId()))
                .thenReturn(List.of(candidatoBaixa, candidatoAlta));
        when(compatibilidadeLlmProvider.calcular(solicitante, List.of(candidatoBaixa, candidatoAlta)))
                .thenReturn(Map.of());
        when(compatibilidadeCalculator.calcular(solicitante.getPerfilAcademico(), candidatoAlta.getPerfilAcademico()))
                .thenReturn(new ResultadoCompatibilidade(90, "Alta", List.of("disciplinas de interesse em comum")));
        when(compatibilidadeCalculator.calcular(solicitante.getPerfilAcademico(), candidatoBaixa.getPerfilAcademico()))
                .thenReturn(new ResultadoCompatibilidade(40, "Baixa", List.of("modalidade de estudo compativel")));

        StudyBuddyMatchingResponseDTO response = service.buscarEstudantesCompativeis(solicitante.getId(), 1);

        assertEquals(solicitante.getId(), response.solicitanteId());
        assertEquals(1, response.total());
        assertEquals(candidatoAlta.getId(), response.candidatos().getFirst().id());
        assertEquals(90, response.candidatos().getFirst().percentualCompatibilidade());
    }

    @Test
    void deveFalharQuandoSolicitanteNaoTemPerfilAcademico() {
        solicitante.setPerfilAcademico(null);
        when(estudanteRepository.findById(solicitante.getId())).thenReturn(Optional.of(solicitante));

        assertThrows(PerfilAcademicoAusenteException.class,
                () -> service.buscarEstudantesCompativeis(solicitante.getId(), 10));
    }

    @Test
    void deveUsarResultadoDaLlmSemExecutarCalculoDeterministico() {
        when(estudanteRepository.findById(solicitante.getId())).thenReturn(Optional.of(solicitante));
        when(estudanteRepository.buscarCandidatosMatching(solicitante.getId()))
                .thenReturn(List.of(candidatoAlta));
        when(compatibilidadeLlmProvider.calcular(solicitante, List.of(candidatoAlta)))
                .thenReturn(Map.of(candidatoAlta.getId(), new ResultadoCompatibilidade(
                        candidatoAlta.getId(), 94, "Resultado da LLM", List.of(), OrigemCompatibilidade.LLM)));

        StudyBuddyMatchingResponseDTO response = service.buscarEstudantesCompativeis(solicitante.getId(), 10);

        assertEquals(94, response.candidatos().getFirst().percentualCompatibilidade());
        assertEquals(OrigemCompatibilidade.LLM, response.candidatos().getFirst().origem());
        verifyNoInteractions(compatibilidadeCalculator);
    }

    @Test
    void deveUsarFallbackDeterministicoQuandoLlmFalha() {
        when(estudanteRepository.findById(solicitante.getId())).thenReturn(Optional.of(solicitante));
        when(estudanteRepository.buscarCandidatosMatching(solicitante.getId()))
                .thenReturn(List.of(candidatoAlta));
        when(compatibilidadeLlmProvider.calcular(solicitante, List.of(candidatoAlta)))
                .thenThrow(new RuntimeException("Groq indisponivel"));
        when(compatibilidadeCalculator.calcular(
                solicitante.getPerfilAcademico(), candidatoAlta.getPerfilAcademico()))
                .thenReturn(new ResultadoCompatibilidade(72, "Fallback", List.of("criterio local")));

        StudyBuddyMatchingResponseDTO response = service.buscarEstudantesCompativeis(solicitante.getId(), 10);

        assertEquals(72, response.candidatos().getFirst().percentualCompatibilidade());
        assertEquals(OrigemCompatibilidade.FALLBACK_DETERMINISTICO,
                response.candidatos().getFirst().origem());
    }

    @Test
    void deveAplicarFallbackSomenteAoCandidatoAusenteNaRespostaDaLlm() {
        when(estudanteRepository.findById(solicitante.getId())).thenReturn(Optional.of(solicitante));
        when(estudanteRepository.buscarCandidatosMatching(solicitante.getId()))
                .thenReturn(List.of(candidatoAlta, candidatoBaixa));
        when(compatibilidadeLlmProvider.calcular(solicitante, List.of(candidatoAlta, candidatoBaixa)))
                .thenReturn(Map.of(candidatoAlta.getId(), new ResultadoCompatibilidade(
                        candidatoAlta.getId(), 90, "Resultado da LLM", List.of(), OrigemCompatibilidade.LLM)));
        when(compatibilidadeCalculator.calcular(
                solicitante.getPerfilAcademico(), candidatoBaixa.getPerfilAcademico()))
                .thenReturn(new ResultadoCompatibilidade(45, "Fallback", List.of("criterio local")));

        StudyBuddyMatchingResponseDTO response = service.buscarEstudantesCompativeis(solicitante.getId(), 10);

        assertEquals(2, response.total());
        assertEquals(OrigemCompatibilidade.LLM, response.candidatos().get(0).origem());
        assertEquals(OrigemCompatibilidade.FALLBACK_DETERMINISTICO, response.candidatos().get(1).origem());
    }

    private Estudante estudante(String nome, PerfilAcademico perfil) {
        Estudante estudante = new Estudante();
        estudante.setId(UUID.randomUUID());
        estudante.setNome(nome);
        estudante.setEmail(nome.toLowerCase().replace(" ", ".") + "@email.com");
        estudante.setTelefone("85999999999");
        estudante.setAtivo(true);
        estudante.setMatricula(UUID.randomUUID().toString());
        estudante.setInstituicao("Universidade Federal");
        estudante.setPerfilAcademico(perfil);
        return estudante;
    }

    private PerfilAcademico perfil(String disciplina) {
        PerfilAcademico perfil = new PerfilAcademico();
        perfil.setCurso("Computacao");
        perfil.setDisciplinasInteresse(Set.of(disciplina));
        perfil.setDisponibilidade(Set.of(PeriodoDisponibilidade.NOITE));
        perfil.setObjetivoEstudo(ObjetivoEstudo.PROVA);
        perfil.setNivelConhecimento(NivelConhecimento.INTERMEDIARIO);
        perfil.setModalidadePreferida(ModalidadeEstudo.ONLINE);
        perfil.setDescricao("Perfil para testes");
        return perfil;
    }
}
