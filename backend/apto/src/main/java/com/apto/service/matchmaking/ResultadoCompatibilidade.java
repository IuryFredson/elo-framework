package com.apto.service.matchmaking;

public record ResultadoCompatibilidade(
        int percentual,
        String justificativa,
        OrigemCompatibilidade origem
) {

    public static ResultadoCompatibilidade fromFramework(
            com.elo.compatibilidade.ResultadoCompatibilidade resultado,
            OrigemCompatibilidade origem) {
        return new ResultadoCompatibilidade(
                resultado.percentual(),
                resultado.justificativa(),
                origem
        );
    }
}
