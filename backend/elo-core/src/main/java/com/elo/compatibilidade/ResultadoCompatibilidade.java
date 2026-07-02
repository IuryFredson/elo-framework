package com.elo.compatibilidade;

import java.util.List;
import java.util.UUID;

public record ResultadoCompatibilidade(
        UUID candidatoId,
        int percentual,
        String justificativa,
        List<String> criteriosAtendidos,
        OrigemCompatibilidade origem
) {
    public ResultadoCompatibilidade(
            int percentual,
            String justificativa,
            List<String> criteriosAtendidos
    ) {
        this(null, percentual, justificativa, criteriosAtendidos, OrigemCompatibilidade.FALLBACK_DETERMINISTICO);
    }
}
