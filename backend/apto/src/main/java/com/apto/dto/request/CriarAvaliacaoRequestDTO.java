package com.apto.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarAvaliacaoRequestDTO(
        @NotNull
        UUID avaliadorId,

        @NotNull
        UUID anuncioId,

        @NotNull
        @Min(1)
        @Max(5)
        Integer notaGeral,

        @NotNull
        @Min(1)
        @Max(5)
        Integer notaComunicacao,

        @NotNull
        @Min(1)
        @Max(5)
        Integer notaFidelidadeAnuncio,

        @NotNull
        @Min(1)
        @Max(5)
        Integer notaEstadoMoradia,

        @NotNull
        @Min(1)
        @Max(5)
        Integer notaCustoBeneficio,

        @Size(max = 1000)
        String comentario
) {}
