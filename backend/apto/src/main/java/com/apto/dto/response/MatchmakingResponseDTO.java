package com.apto.dto.response;

import java.util.List;
import java.util.UUID;

public record MatchmakingResponseDTO(
        UUID solicitanteId,
        int total,
        List<MatchColegaResponseDTO> candidatos
) {}