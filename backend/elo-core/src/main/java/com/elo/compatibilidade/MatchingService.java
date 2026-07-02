package com.elo.compatibilidade;

import com.elo.perfil.Perfil;
import com.elo.usuario.Usuario;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class MatchingService<U extends Usuario, P extends Perfil> {

    private final CompatibilidadeStrategy<P> estrategiaDeterministica;
    private final ProvedorCompatibilidadeLlm<U, P> provedorLlm;

    protected MatchingService(
            CompatibilidadeStrategy<P> estrategiaDeterministica,
            ProvedorCompatibilidadeLlm<U, P> provedorLlm
    ) {
        this.estrategiaDeterministica = Objects.requireNonNull(
                estrategiaDeterministica,
                "estrategiaDeterministica não deve ser null"
        );
        this.provedorLlm = Objects.requireNonNull(provedorLlm, "provedorLlm não deve ser null");
    }

    public List<ResultadoMatching<U>> calcularMatches(UUID solicitanteId, int topN) {
        if (topN < 0) {
            throw new IllegalArgumentException("topN deve ser maior ou igual a zero");
        }

        U solicitante = buscarSolicitante(solicitanteId);
        validarPerfilSolicitante(solicitante);

        List<U> candidatos = buscarCandidatos(solicitanteId).stream()
                .filter(candidato -> elegivel(solicitante, candidato))
                .toList();

        if (candidatos.isEmpty() || topN == 0) {
            return List.of();
        }

        Map<UUID, ResultadoCompatibilidade> resultadosLlm = calcularComLlm(solicitante, candidatos);

        return candidatos.stream()
                .map(candidato -> calcularResultado(solicitante, candidato, resultadosLlm))
                .sorted(Comparator.comparingInt(
                        (ResultadoMatching<U> resultado) -> resultado.compatibilidade().percentual()
                ).reversed())
                .limit(topN)
                .toList();
    }

    protected abstract U buscarSolicitante(UUID solicitanteId);

    protected abstract List<U> buscarCandidatos(UUID solicitanteId);

    protected abstract P perfil(U usuario);

    protected abstract boolean elegivel(U solicitante, U candidato);

    protected abstract void validarPerfilSolicitante(U solicitante);

    protected void aoUsarLlm(U solicitante, List<U> candidatos) {
    }

    protected void aoUsarFallback(U solicitante, RuntimeException causa) {
    }

    private Map<UUID, ResultadoCompatibilidade> calcularComLlm(U solicitante, List<U> candidatos) {
        try {
            Map<UUID, ResultadoCompatibilidade> resultados = provedorLlm.calcular(solicitante, candidatos);
            if (resultados == null || resultados.isEmpty()) {
                return Map.of();
            }
            aoUsarLlm(solicitante, candidatos);
            return resultados;
        } catch (RuntimeException e) {
            aoUsarFallback(solicitante, e);
            return Map.of();
        }
    }

    private ResultadoMatching<U> calcularResultado(
            U solicitante,
            U candidato,
            Map<UUID, ResultadoCompatibilidade> resultadosLlm
    ) {
        ResultadoCompatibilidade resultado = resultadosLlm.get(candidato.getId());
        if (resultado == null) {
            resultado = calcularFallback(solicitante, candidato);
        } else {
            resultado = normalizarResultado(candidato, resultado, OrigemCompatibilidade.LLM);
        }

        return new ResultadoMatching<>(candidato, resultado);
    }

    private ResultadoCompatibilidade calcularFallback(U solicitante, U candidato) {
        return normalizarResultado(
                candidato,
                estrategiaDeterministica.calcular(perfil(solicitante), perfil(candidato)),
                OrigemCompatibilidade.FALLBACK_DETERMINISTICO
        );
    }

    private ResultadoCompatibilidade normalizarResultado(
            U candidato,
            ResultadoCompatibilidade resultado,
            OrigemCompatibilidade origem
    ) {
        return new ResultadoCompatibilidade(
                candidato.getId(),
                resultado.percentual(),
                resultado.justificativa(),
                resultado.criteriosAtendidos(),
                origem
        );
    }
}
