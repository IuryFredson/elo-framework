package com.apto.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarManifestacaoInteresseRequestDTO(

        @NotNull
        UUID anuncioId,

        @NotNull
        UUID interessadoId,

        @Size(max = 500)
        String mensagem
) {}
