package com.studybuddy.controller;

import com.elo.compatibilidade.OrigemCompatibilidade;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.studybuddy.dto.request.AtualizarPerfilAcademicoRequestDTO;
import com.studybuddy.dto.request.CriarEstudanteRequestDTO;
import com.studybuddy.dto.request.CriarGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.EstudanteResponseDTO;
import com.studybuddy.dto.response.GrupoEstudoResponseDTO;
import com.studybuddy.dto.response.ManifestacaoInteresseGrupoResponseDTO;
import com.studybuddy.dto.response.MatchEstudanteResponseDTO;
import com.studybuddy.dto.response.PerfilAcademicoResponseDTO;
import com.studybuddy.dto.response.StudyBuddyMatchingResponseDTO;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.NivelConhecimento;
import com.studybuddy.model.enums.ObjetivoEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import com.studybuddy.model.enums.StatusGrupoEstudo;
import com.studybuddy.service.EstudanteService;
import com.studybuddy.service.GrupoEstudoService;
import com.studybuddy.service.ManifestacaoInteresseGrupoService;
import com.studybuddy.service.PerfilAcademicoService;
import com.studybuddy.service.matching.StudyBuddyMatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudyBuddyControllerTest {

    @Test
    void deveCriarEstudanteDelegandoParaService() {
        EstudanteService service = mock(EstudanteService.class);
        EstudanteController controller = new EstudanteController(service);
        UUID estudanteId = UUID.randomUUID();
        CriarEstudanteRequestDTO request = new CriarEstudanteRequestDTO(
                "Ana Souza",
                "ana@ufc.br",
                "85999999999",
                "20240001",
                "UFC");
        EstudanteResponseDTO esperado = new EstudanteResponseDTO(
                estudanteId,
                "Ana Souza",
                "ana@ufc.br",
                "85999999999",
                true,
                "20240001",
                "UFC");
        when(service.criar(request)).thenReturn(esperado);

        ResponseEntity<EstudanteResponseDTO> resposta = controller.criar(request);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(esperado, resposta.getBody());
        verify(service).criar(request);
        assertEquals("/study-buddy/usuarios", rotaBase(EstudanteController.class));
    }

    @Test
    void deveAtualizarPerfilAcademicoDelegandoParaService() {
        PerfilAcademicoService service = mock(PerfilAcademicoService.class);
        PerfilAcademicoController controller = new PerfilAcademicoController(service);
        UUID estudanteId = UUID.randomUUID();
        AtualizarPerfilAcademicoRequestDTO request = new AtualizarPerfilAcademicoRequestDTO(
                "Computacao",
                Set.of("Projeto Detalhado de Software"),
                Set.of(PeriodoDisponibilidade.NOITE),
                ObjetivoEstudo.PROVA,
                NivelConhecimento.INTERMEDIARIO,
                ModalidadeEstudo.ONLINE,
                "Busca grupo para estudar PDS.");
        PerfilAcademicoResponseDTO esperado = new PerfilAcademicoResponseDTO(
                estudanteId,
                "Ana Souza",
                "ana@ufc.br",
                "20240001",
                "UFC",
                "Computacao",
                request.disciplinasInteresse(),
                request.disponibilidade(),
                request.objetivoEstudo(),
                request.nivelConhecimento(),
                request.modalidadePreferida(),
                request.descricao());
        when(service.atualizarPerfil(estudanteId, request)).thenReturn(esperado);

        ResponseEntity<PerfilAcademicoResponseDTO> resposta = controller.atualizarPerfil(estudanteId, request);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(esperado, resposta.getBody());
        verify(service).atualizarPerfil(estudanteId, request);
        assertEquals("/study-buddy/usuarios", rotaBase(PerfilAcademicoController.class));
    }

    @Test
    void deveCriarGrupoDeEstudoDelegandoParaService() {
        GrupoEstudoService service = mock(GrupoEstudoService.class);
        GrupoEstudoController controller = new GrupoEstudoController(service);
        UUID grupoId = UUID.randomUUID();
        UUID publicadorId = UUID.randomUUID();
        CriarGrupoEstudoRequestDTO request = new CriarGrupoEstudoRequestDTO(
                publicadorId,
                "Grupo de PDS",
                "Estudos para a entrega do framework",
                "Projeto Detalhado de Software",
                4,
                ModalidadeEstudo.HIBRIDO,
                PeriodoDisponibilidade.NOITE);
        GrupoEstudoResponseDTO esperado = new GrupoEstudoResponseDTO(
                grupoId,
                request.titulo(),
                request.descricao(),
                request.disciplina(),
                publicadorId,
                "Ana Souza",
                request.quantidadeVagas(),
                request.modalidade(),
                request.periodo(),
                StatusGrupoEstudo.ATIVO,
                LocalDate.now());
        when(service.criar(request)).thenReturn(esperado);

        ResponseEntity<GrupoEstudoResponseDTO> resposta = controller.criar(request);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(esperado, resposta.getBody());
        verify(service).criar(request);
        assertEquals("/study-buddy/ofertas", rotaBase(GrupoEstudoController.class));
    }

    @Test
    void deveAceitarManifestacaoDeInteresseDelegandoParaService() {
        ManifestacaoInteresseGrupoService service = mock(ManifestacaoInteresseGrupoService.class);
        ManifestacaoInteresseGrupoController controller = new ManifestacaoInteresseGrupoController(service);
        UUID manifestacaoId = UUID.randomUUID();
        UUID grupoId = UUID.randomUUID();
        UUID interessadoId = UUID.randomUUID();
        UUID publicadorId = UUID.randomUUID();
        ManifestacaoInteresseGrupoResponseDTO esperado = new ManifestacaoInteresseGrupoResponseDTO(
                manifestacaoId,
                grupoId,
                "Grupo de PDS",
                interessadoId,
                "Bruno Lima",
                publicadorId,
                "Ana Souza",
                StatusManifestacaoInteresse.ACEITA,
                "Tenho interesse.",
                LocalDateTime.now(),
                LocalDateTime.now());
        when(service.aceitar(manifestacaoId, publicadorId)).thenReturn(esperado);

        ResponseEntity<ManifestacaoInteresseGrupoResponseDTO> resposta =
                controller.aceitar(manifestacaoId, publicadorId);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(esperado, resposta.getBody());
        verify(service).aceitar(manifestacaoId, publicadorId);
        assertEquals("/study-buddy/manifestacoes", rotaBase(ManifestacaoInteresseGrupoController.class));
    }

    @Test
    void deveBuscarMatchingDelegandoParaService() {
        StudyBuddyMatchingService service = mock(StudyBuddyMatchingService.class);
        StudyBuddyMatchingController controller = new StudyBuddyMatchingController(service);
        UUID solicitanteId = UUID.randomUUID();
        UUID candidatoId = UUID.randomUUID();
        MatchEstudanteResponseDTO candidato = new MatchEstudanteResponseDTO(
                candidatoId,
                "Bruno Lima",
                "Computacao",
                "UFC",
                Set.of("Projeto Detalhado de Software"),
                ObjetivoEstudo.PROVA,
                NivelConhecimento.INTERMEDIARIO,
                ModalidadeEstudo.ONLINE,
                90,
                "Compatibilidade academica alta.",
                List.of("disciplina em comum"),
                OrigemCompatibilidade.FALLBACK_DETERMINISTICO);
        StudyBuddyMatchingResponseDTO esperado = new StudyBuddyMatchingResponseDTO(
                solicitanteId,
                1,
                List.of(candidato));
        when(service.buscarEstudantesCompativeis(solicitanteId, 5)).thenReturn(esperado);

        ResponseEntity<StudyBuddyMatchingResponseDTO> resposta =
                controller.buscarMatches(solicitanteId, 5);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(esperado, resposta.getBody());
        verify(service).buscarEstudantesCompativeis(solicitanteId, 5);
        assertEquals("/study-buddy/matching", rotaBase(StudyBuddyMatchingController.class));
    }

    private String rotaBase(Class<?> controllerClass) {
        return controllerClass.getAnnotation(RequestMapping.class).value()[0];
    }
}
