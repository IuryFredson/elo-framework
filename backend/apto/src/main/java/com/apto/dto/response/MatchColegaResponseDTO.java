package com.apto.dto.response;

import com.apto.service.matchmaking.OrigemCompatibilidade;
import com.apto.model.enums.Genero;

import java.util.UUID;

public record MatchColegaResponseDTO(
        UUID id,
        String nome,
        String curso,
        Genero genero,
        int percentualCompatibilidade,
        String justificativa,
        OrigemCompatibilidade origem
) {}