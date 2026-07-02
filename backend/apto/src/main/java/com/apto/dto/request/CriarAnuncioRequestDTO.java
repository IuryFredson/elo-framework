package com.apto.dto.request;

import com.apto.model.enums.TipoAnuncio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record CriarAnuncioRequestDTO(

        @NotBlank
        String titulo,

        @NotBlank
        String descricao,

        @PositiveOrZero
        @NotNull
        BigDecimal valorMensal,

        @NotNull
        TipoAnuncio tipoAnuncio,

        @NotNull
        UUID moradiaId,

        @NotNull
        UUID anuncianteId
) {}