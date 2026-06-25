package com.apto.dto.request;

import com.apto.model.enums.StatusDenuncia;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequestDTO(@NotNull StatusDenuncia novoStatus) {
}
