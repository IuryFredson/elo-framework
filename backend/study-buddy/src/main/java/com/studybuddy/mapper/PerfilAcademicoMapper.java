package com.studybuddy.mapper;

import com.elo.porta.MapperResposta;
import com.studybuddy.dto.response.PerfilAcademicoResponseDTO;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import org.springframework.stereotype.Component;

@Component
public class PerfilAcademicoMapper implements MapperResposta<Estudante, PerfilAcademicoResponseDTO> {

    @Override
    public PerfilAcademicoResponseDTO paraResposta(Estudante estudante) {
        return toResponseDTO(estudante);
    }

    public PerfilAcademicoResponseDTO toResponseDTO(Estudante estudante) {
        PerfilAcademico perfil = estudante.getPerfilAcademico();

        return new PerfilAcademicoResponseDTO(
                estudante.getId(),
                estudante.getNome(),
                estudante.getEmail(),
                estudante.getMatricula(),
                estudante.getInstituicao(),
                perfil != null ? perfil.getCurso() : null,
                perfil != null ? perfil.getDisciplinasInteresse() : null,
                perfil != null ? perfil.getDisponibilidade() : null,
                perfil != null ? perfil.getObjetivoEstudo() : null,
                perfil != null ? perfil.getNivelConhecimento() : null,
                perfil != null ? perfil.getModalidadePreferida() : null,
                perfil != null ? perfil.getDescricao() : null
        );
    }
}
