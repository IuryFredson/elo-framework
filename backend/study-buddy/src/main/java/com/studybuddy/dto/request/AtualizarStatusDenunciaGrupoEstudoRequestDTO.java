package com.studybuddy.dto.request;

import com.elo.denuncia.StatusDenuncia;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusDenunciaGrupoEstudoRequestDTO(
        @NotNull StatusDenuncia novoStatus
) {
}
