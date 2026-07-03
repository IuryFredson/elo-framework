package com.studybuddy.mapper;

import com.studybuddy.dto.response.DenunciaGrupoEstudoResponseDTO;
import com.studybuddy.model.entity.DenunciaGrupoEstudo;
import org.springframework.stereotype.Component;

@Component
public class DenunciaGrupoEstudoMapper {

    public DenunciaGrupoEstudoResponseDTO toResponseDTO(DenunciaGrupoEstudo denuncia) {
        return new DenunciaGrupoEstudoResponseDTO(
                denuncia.getId(),
                denuncia.getDenunciante().getId(),
                denuncia.getGrupoEstudo().getId(),
                denuncia.getTitulo(),
                denuncia.getCorpo(),
                denuncia.getCriterio() == null ? com.studybuddy.model.enums.CriterioDenunciaStudyBuddy.OUTRO : denuncia.getCriterio(),
                denuncia.getStatusDenuncia(),
                denuncia.getCriadoEm()
        );
    }
}
