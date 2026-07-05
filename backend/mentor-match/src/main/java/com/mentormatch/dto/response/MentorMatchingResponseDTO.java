package com.mentormatch.dto.response;

import java.util.List;
import java.util.UUID;

public record MentorMatchingResponseDTO(UUID solicitanteId, int total, List<MatchMentorResponseDTO> mentores) { }
