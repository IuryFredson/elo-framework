package com.apto.dto.response;

import com.apto.model.enums.StatusManifestacaoInteresse;

import java.time.LocalDateTime;
import java.util.UUID;

public record ManifestacaoInteresseResponseDTO(
        UUID id,
        UUID anuncioId,
        String anuncioTitulo,
        UUID interessadoId,
        String interessadoNome,
        StatusManifestacaoInteresse status,
        String mensagem,
        LocalDateTime dataManifestacao,
        LocalDateTime dataResposta
) {}
