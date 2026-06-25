package com.apto.dto.response;

import com.apto.model.enums.AcaoModeracaoAnuncio;
import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.StatusDenuncia;

import java.time.LocalDateTime;
import java.util.UUID;

public record ModeracaoResponseDTO(
        UUID denunciaId,
        UUID anuncioId,
        StatusDenuncia statusDenunciaAnterior,
        StatusDenuncia statusDenunciaAtual,
        AcaoModeracaoAnuncio acaoAnuncioAplicada,
        StatusAnuncio statusAnuncioAnterior,
        StatusAnuncio statusAnuncioAtual,
        String justificativa,
        LocalDateTime moderadoEm
) {}