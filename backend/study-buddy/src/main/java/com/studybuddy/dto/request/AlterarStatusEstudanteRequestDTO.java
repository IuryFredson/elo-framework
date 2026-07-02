package com.studybuddy.dto.request;

import jakarta.validation.constraints.NotNull;

public record AlterarStatusEstudanteRequestDTO(
        @NotNull Boolean ativo
) {
}
