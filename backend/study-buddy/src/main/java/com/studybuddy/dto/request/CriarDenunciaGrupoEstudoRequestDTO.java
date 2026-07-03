package com.studybuddy.dto.request;

import com.studybuddy.model.enums.CriterioDenunciaStudyBuddy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarDenunciaGrupoEstudoRequestDTO(
        @NotNull UUID denuncianteId,
        @NotNull UUID grupoId,
        @NotBlank @Size(min = 2, max = 255) String titulo,
        @NotBlank @Size(min = 2, max = 1000) String corpo,
        CriterioDenunciaStudyBuddy criterio
) {
    public CriarDenunciaGrupoEstudoRequestDTO(UUID denuncianteId, UUID grupoId, String titulo, String corpo) {
        this(denuncianteId, grupoId, titulo, corpo, CriterioDenunciaStudyBuddy.OUTRO);
    }

    public CriterioDenunciaStudyBuddy criterioOuOutro() {
        return criterio == null ? CriterioDenunciaStudyBuddy.OUTRO : criterio;
    }
}
