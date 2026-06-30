package com.elo.compatibilidade;

public record ResultadoMatching<C>(
        C candidato,
        ResultadoCompatibilidade compatibilidade
) {
}
