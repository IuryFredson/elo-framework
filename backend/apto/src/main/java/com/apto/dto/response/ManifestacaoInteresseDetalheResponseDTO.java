package com.apto.dto.response;

import com.apto.model.enums.StatusManifestacaoInteresse;

import java.time.LocalDateTime;
import java.util.UUID;

public record ManifestacaoInteresseDetalheResponseDTO(
        UUID id,
        UUID anuncioId,
        String anuncioTitulo,
        UUID interessadoId,
        String interessadoNome,
        StatusManifestacaoInteresse status,
        String mensagem,
        LocalDateTime dataManifestacao,
        LocalDateTime dataResposta,
        ContatoLiberadoResponseDTO contatoInteressado,
        ContatoLiberadoResponseDTO contatoAnunciante
) {}
