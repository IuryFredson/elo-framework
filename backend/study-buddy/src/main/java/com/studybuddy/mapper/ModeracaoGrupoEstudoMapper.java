package com.studybuddy.mapper;

import com.elo.denuncia.StatusDenuncia;
import com.studybuddy.dto.request.ModerarDenunciaGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.ModeracaoGrupoEstudoResponseDTO;
import com.studybuddy.model.entity.DenunciaGrupoEstudo;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.enums.StatusGrupoEstudo;
import org.springframework.stereotype.Component;

@Component
public class ModeracaoGrupoEstudoMapper {

    public ModeracaoGrupoEstudoResponseDTO toResponseDTO(
            DenunciaGrupoEstudo denuncia,
            GrupoEstudo grupoEstudo,
            StatusDenuncia statusAnteriorDenuncia,
            StatusGrupoEstudo statusAnteriorGrupo,
            ModerarDenunciaGrupoEstudoRequestDTO dto) {
        return new ModeracaoGrupoEstudoResponseDTO(
                denuncia.getId(),
                grupoEstudo.getId(),
                statusAnteriorDenuncia,
                denuncia.getStatusDenuncia(),
                dto.acaoGrupoEstudo(),
                statusAnteriorGrupo,
                grupoEstudo.getStatus(),
                dto.justificativa(),
                denuncia.getStatusAtualizadoEm()
        );
    }
}
