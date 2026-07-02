package com.studybuddy.mapper;

import com.elo.porta.MapperResposta;
import com.studybuddy.dto.response.GrupoEstudoResponseDTO;
import com.studybuddy.model.entity.GrupoEstudo;
import org.springframework.stereotype.Component;

@Component
public class GrupoEstudoMapper implements MapperResposta<GrupoEstudo, GrupoEstudoResponseDTO> {

    @Override
    public GrupoEstudoResponseDTO paraResposta(GrupoEstudo grupo) {
        return toResponseDTO(grupo);
    }

    public GrupoEstudoResponseDTO toResponseDTO(GrupoEstudo grupo) {
        return new GrupoEstudoResponseDTO(
                grupo.getId(),
                grupo.getTitulo(),
                grupo.getDescricao(),
                grupo.getDisciplina(),
                grupo.getPublicadorId(),
                grupo.getPublicador().getNome(),
                grupo.getQuantidadeVagas(),
                grupo.getModalidade(),
                grupo.getPeriodo(),
                grupo.getStatus(),
                grupo.getDataPublicacao()
        );
    }
}
