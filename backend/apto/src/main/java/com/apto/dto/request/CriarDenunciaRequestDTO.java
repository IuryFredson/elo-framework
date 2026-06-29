package com.apto.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarDenunciaRequestDTO(

    @NotNull
    UUID denuncianteId,

    @NotNull
    UUID anuncioId,

    @Size(min = 2, max = 255)
    @NotBlank
    String titulo,
    @Size(min = 2, max = 1000)
    @NotBlank
    String corpo
){}
