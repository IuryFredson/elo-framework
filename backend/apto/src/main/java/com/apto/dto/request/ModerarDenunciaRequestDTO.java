package com.apto.dto.request;

import com.apto.model.enums.AcaoModeracaoAnuncio;
import com.apto.model.enums.StatusDenuncia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModerarDenunciaRequestDTO(

        @NotNull
        StatusDenuncia novoStatus,

        @NotNull
        AcaoModeracaoAnuncio acaoAnuncio,

        @Size(max = 1000)
        String justificativa
) {}