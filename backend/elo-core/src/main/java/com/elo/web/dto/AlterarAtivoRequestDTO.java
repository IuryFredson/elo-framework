package com.elo.web.dto;

import jakarta.validation.constraints.NotNull;

public record AlterarAtivoRequestDTO(
        @NotNull Boolean ativo
) {
}
