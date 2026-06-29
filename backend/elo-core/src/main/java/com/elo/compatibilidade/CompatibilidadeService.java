package com.elo.compatibilidade;

import com.elo.perfil.Perfil;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

public class CompatibilidadeService<P extends Perfil> {

	private final CompatibilidadeStrategy<P> strategy;

	public CompatibilidadeService(CompatibilidadeStrategy<P> strategy) {
		this.strategy = Objects.requireNonNull(strategy, "strategy não deve ser null");
	}

	public List<ResultadoCompatibilidade> calcularCompatibilidades(
		P solicitante,
		List<P> candidatos,
		BiPredicate<P, P> elegivel,
		int topN
	) {
		Objects.requireNonNull(solicitante, "solicitante não deve ser null");
		Objects.requireNonNull(candidatos, "candidatos não deve ser null");
		Objects.requireNonNull(elegivel, "elegivel não deve ser null");

		if (topN < 0) {
			throw new IllegalArgumentException("topN deve ser maior ou igual a zero");
		}

		return candidatos.stream()
			.filter(candidato -> elegivel.test(solicitante, candidato))
			.map(candidato -> strategy.calcular(solicitante, candidato))
			.sorted(Comparator.comparingInt(ResultadoCompatibilidade::percentual).reversed())
			.limit(topN)
			.toList();
	}
}
