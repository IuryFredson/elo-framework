package com.apto.service.matchmaking;

public record ResultadoCompatibilidade(
        int percentual,
        String justificativa,
        OrigemCompatibilidade origem
) {

    public static ResultadoCompatibilidade fromFramework(
            com.elo.compatibilidade.ResultadoCompatibilidade resultado) {
        return new ResultadoCompatibilidade(
                resultado.percentual(),
                resultado.justificativa(),
                origemApto(resultado.origem())
        );
    }

    public static ResultadoCompatibilidade fromFramework(
            com.elo.compatibilidade.ResultadoCompatibilidade resultado,
            OrigemCompatibilidade origem) {
        return new ResultadoCompatibilidade(
                resultado.percentual(),
                resultado.justificativa(),
                origem
        );
    }

    private static OrigemCompatibilidade origemApto(com.elo.compatibilidade.OrigemCompatibilidade origem) {
        return switch (origem) {
            case LLM -> OrigemCompatibilidade.LLM;
            case FALLBACK_DETERMINISTICO -> OrigemCompatibilidade.FALLBACK_DETERMINISTICO;
        };
    }
}
