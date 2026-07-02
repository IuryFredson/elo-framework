package com.studybuddy.service.matching;

import com.elo.compatibilidade.CompatibilidadeStrategy;
import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.studybuddy.model.entity.PerfilAcademico;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.NivelConhecimento;
import com.studybuddy.model.enums.ObjetivoEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CompatibilidadeAcademicaCalculator implements CompatibilidadeStrategy<PerfilAcademico> {

    private static final String JUSTIFICATIVA_PADRAO =
            "Compatibilidade calculada por criterios academicos deterministicos.";

    @Override
    public ResultadoCompatibilidade calcular(PerfilAcademico solicitante, PerfilAcademico candidato) {
        if (solicitante == null || candidato == null) {
            return new ResultadoCompatibilidade(0, JUSTIFICATIVA_PADRAO, List.of());
        }

        List<String> criteriosAtendidos = new ArrayList<>();
        int pontuacao = 0;

        int disciplinas = scoreDisciplinas(solicitante, candidato);
        pontuacao += disciplinas;
        if (disciplinas > 0) {
            criteriosAtendidos.add("disciplinas de interesse em comum");
        }

        int disponibilidade = scoreDisponibilidade(solicitante, candidato);
        pontuacao += disponibilidade;
        if (disponibilidade > 0) {
            criteriosAtendidos.add("disponibilidade compativel");
        }

        int objetivo = scoreObjetivo(solicitante.getObjetivoEstudo(), candidato.getObjetivoEstudo());
        pontuacao += objetivo;
        if (objetivo > 0) {
            criteriosAtendidos.add("objetivo de estudo compativel");
        }

        int nivel = scoreNivel(solicitante.getNivelConhecimento(), candidato.getNivelConhecimento());
        pontuacao += nivel;
        if (nivel > 0) {
            criteriosAtendidos.add("nivel de conhecimento proximo");
        }

        int modalidade = scoreModalidade(solicitante.getModalidadePreferida(), candidato.getModalidadePreferida());
        pontuacao += modalidade;
        if (modalidade > 0) {
            criteriosAtendidos.add("modalidade de estudo compativel");
        }

        return new ResultadoCompatibilidade(
                clamp(pontuacao, 0, 100),
                JUSTIFICATIVA_PADRAO,
                criteriosAtendidos
        );
    }

    private int scoreDisciplinas(PerfilAcademico solicitante, PerfilAcademico candidato) {
        Set<String> solicitanteDisciplinas = normalizar(solicitante.getDisciplinasInteresse());
        Set<String> candidatoDisciplinas = normalizar(candidato.getDisciplinasInteresse());
        solicitanteDisciplinas.retainAll(candidatoDisciplinas);
        return solicitanteDisciplinas.isEmpty() ? 0 : 35;
    }

    private int scoreDisponibilidade(PerfilAcademico solicitante, PerfilAcademico candidato) {
        Set<PeriodoDisponibilidade> disponibilidadeSolicitante = solicitante.getDisponibilidade();
        Set<PeriodoDisponibilidade> disponibilidadeCandidato = candidato.getDisponibilidade();
        if (disponibilidadeSolicitante == null || disponibilidadeCandidato == null) {
            return 0;
        }
        if (disponibilidadeSolicitante.contains(PeriodoDisponibilidade.FLEXIVEL)
                || disponibilidadeCandidato.contains(PeriodoDisponibilidade.FLEXIVEL)) {
            return 25;
        }
        Set<PeriodoDisponibilidade> interseccao = new HashSet<>(disponibilidadeSolicitante);
        interseccao.retainAll(disponibilidadeCandidato);
        return interseccao.isEmpty() ? 0 : 25;
    }

    private int scoreObjetivo(ObjetivoEstudo solicitante, ObjetivoEstudo candidato) {
        if (solicitante == null || candidato == null) {
            return 0;
        }
        if (solicitante == candidato) {
            return 15;
        }
        return objetivosComplementares(solicitante, candidato) ? 10 : 0;
    }

    private boolean objetivosComplementares(ObjetivoEstudo a, ObjetivoEstudo b) {
        return (a == ObjetivoEstudo.PROVA && b == ObjetivoEstudo.REFORCO)
                || (a == ObjetivoEstudo.REFORCO && b == ObjetivoEstudo.PROVA)
                || (a == ObjetivoEstudo.PROJETO && b == ObjetivoEstudo.TRABALHO)
                || (a == ObjetivoEstudo.TRABALHO && b == ObjetivoEstudo.PROJETO);
    }

    private int scoreNivel(NivelConhecimento solicitante, NivelConhecimento candidato) {
        if (solicitante == null || candidato == null) {
            return 0;
        }
        int distancia = Math.abs(solicitante.ordinal() - candidato.ordinal());
        return switch (distancia) {
            case 0 -> 15;
            case 1 -> 8;
            default -> 0;
        };
    }

    private int scoreModalidade(ModalidadeEstudo solicitante, ModalidadeEstudo candidato) {
        if (solicitante == null || candidato == null) {
            return 0;
        }
        if (solicitante == candidato) {
            return 10;
        }
        return solicitante == ModalidadeEstudo.HIBRIDO || candidato == ModalidadeEstudo.HIBRIDO ? 7 : 0;
    }

    private Set<String> normalizar(Set<String> disciplinas) {
        if (disciplinas == null) {
            return new HashSet<>();
        }
        Set<String> normalizadas = new HashSet<>();
        for (String disciplina : disciplinas) {
            if (disciplina != null && !disciplina.isBlank()) {
                normalizadas.add(disciplina.trim().toLowerCase());
            }
        }
        return normalizadas;
    }

    private int clamp(int valor, int min, int max) {
        return Math.max(min, Math.min(max, valor));
    }
}
