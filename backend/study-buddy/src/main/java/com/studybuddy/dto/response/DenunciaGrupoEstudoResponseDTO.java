package com.studybuddy.dto.response;

import com.elo.denuncia.StatusDenuncia;
import com.studybuddy.model.enums.CriterioDenunciaStudyBuddy;

import java.time.LocalDateTime;
import java.util.UUID;

public record DenunciaGrupoEstudoResponseDTO(
        UUID id,
        UUID denuncianteId,
        UUID grupoId,
        String titulo,
        String corpo,
        CriterioDenunciaStudyBuddy criterio,
        StatusDenuncia status,
        LocalDateTime criadoEm
) {
}
