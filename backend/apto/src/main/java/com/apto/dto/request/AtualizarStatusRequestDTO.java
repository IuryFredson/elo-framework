package com.apto.dto.request;

import com.elo.denuncia.StatusDenuncia;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequestDTO(@NotNull StatusDenuncia novoStatus) {
}
