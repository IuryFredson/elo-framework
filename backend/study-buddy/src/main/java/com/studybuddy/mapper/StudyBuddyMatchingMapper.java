package com.studybuddy.mapper;

import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.studybuddy.dto.response.MatchEstudanteResponseDTO;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import org.springframework.stereotype.Component;

@Component
public class StudyBuddyMatchingMapper {

    public MatchEstudanteResponseDTO toEstudanteResponseDTO(
            Estudante candidato,
            ResultadoCompatibilidade resultado) {
        PerfilAcademico perfil = candidato.getPerfilAcademico();
        return new MatchEstudanteResponseDTO(
                candidato.getId(),
                candidato.getNome(),
                perfil.getCurso(),
                candidato.getInstituicao(),
                perfil.getDisciplinasInteresse(),
                perfil.getObjetivoEstudo(),
                perfil.getNivelConhecimento(),
                perfil.getModalidadePreferida(),
                resultado.percentual(),
                resultado.justificativa(),
                resultado.criteriosAtendidos(),
                resultado.origem()
        );
    }
}
