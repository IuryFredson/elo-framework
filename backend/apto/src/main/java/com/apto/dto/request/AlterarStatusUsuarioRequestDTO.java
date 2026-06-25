package com.apto.dto.request;

import jakarta.validation.constraints.NotNull;

public record AlterarStatusUsuarioRequestDTO(
        @NotNull Boolean ativo
) {}