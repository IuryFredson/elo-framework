package com.apto.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.apto.model.enums.TipoAnuncio;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AtualizarAnuncioRequestDTO(
        @NotBlank
        String titulo,

        @NotBlank
        String descricao,

        @PositiveOrZero
        @NotNull
        BigDecimal valorMensal,

        @NotNull
        TipoAnuncio tipoAnuncio
) {}
