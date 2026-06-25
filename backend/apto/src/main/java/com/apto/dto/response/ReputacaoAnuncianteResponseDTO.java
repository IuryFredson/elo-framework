package com.apto.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReputacaoAnuncianteResponseDTO(
        UUID id,
        UUID perfilAnuncianteId,
        UUID usuarioId,             // id do Usuario (Locador ou Universitário)
        Double reputacaoScore,
        Integer totalAvaliacoes,
        Double mediaGeral,
        Double mediaComunicacao,
        Double mediaFidelidadeAnuncio,
        Double mediaEstadoMoradia,
        Double mediaCustoBeneficio,
        LocalDateTime ultimaAtualizacao
) {}
