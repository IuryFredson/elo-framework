package com.studybuddy.dto.response;

import java.util.List;
import java.util.UUID;

public record StudyBuddyMatchingResponseDTO(
        UUID solicitanteId,
        int total,
        List<MatchEstudanteResponseDTO> candidatos
) {
}
