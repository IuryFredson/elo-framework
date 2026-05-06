package com.apto.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReputacaoLocadorResponseDTO (
    UUID id,
    UUID locadorId,
    Double reputacaoScore,
    Integer totalAvaliacoes,
    Double mediaGeral,
    Double mediaComunicacao,
    Double mediaFidelidadeAnuncio,
    Double mediaEstadoMoradia,
    Double mediaCustoBeneficio,
    LocalDateTime ultimaAtualizacao
)
{}
