package com.apto.dto.request;

import com.apto.model.enums.TipoMoradia;
import jakarta.validation.constraints.*;

public record AtualizarMoradiaRequestDTO(
        @NotNull
        TipoMoradia tipoMoradia,

        @NotBlank
        @Size(min = 2, max = 255)
        String bairro,

        @NotBlank
        @Size(min = 2, max = 255)
        String enderecoResumo,

        @NotNull
        Boolean mobiliado,

        @NotNull
        Boolean aceitaAnimais,

        @NotNull
        @PositiveOrZero
        Integer quantidadeVagas,

        @NotBlank
        @Size(min = 2, max = 255)
        String regrasMoradia
) {}
