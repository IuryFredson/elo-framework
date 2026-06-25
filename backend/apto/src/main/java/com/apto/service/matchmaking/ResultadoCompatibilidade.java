package com.apto.service.matchmaking;

public record ResultadoCompatibilidade(
        int percentual,
        String justificativa,
        OrigemCompatibilidade origem
) {}