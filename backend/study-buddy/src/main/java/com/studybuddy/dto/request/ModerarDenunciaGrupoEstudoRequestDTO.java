package com.studybuddy.dto.request;

import com.elo.denuncia.StatusDenuncia;
import com.studybuddy.model.enums.AcaoModeracaoGrupoEstudo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModerarDenunciaGrupoEstudoRequestDTO(
        @NotNull StatusDenuncia novoStatus,
        @NotNull AcaoModeracaoGrupoEstudo acaoGrupoEstudo,
        @Size(max = 1000) String justificativa
) {
}
