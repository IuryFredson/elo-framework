package com.elo.compatibilidade;

import com.elo.perfil.Perfil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompatibilidadeServiceTest {

	@Test
	void calcularCompatibilidadesFiltraCalculaOrdenaELimitaResultados() {
		FakePerfil solicitante = new FakePerfil("origem", 0, true);
		FakePerfil primeiroElegivel = new FakePerfil("primeiro", 40, true);
		FakePerfil inelegivel = new FakePerfil("inelegivel", 100, false);
		FakePerfil melhorElegivel = new FakePerfil("melhor", 90, true);
		FakePerfil segundoElegivel = new FakePerfil("segundo", 70, true);
		FakeStrategy strategy = new FakeStrategy();
		CompatibilidadeService<FakePerfil> service = new CompatibilidadeService<>(strategy);
		BiPredicate<FakePerfil, FakePerfil> elegivel = (origem, destino) -> destino.elegivel();

		List<ResultadoCompatibilidade> resultados = service.calcularCompatibilidades(
			solicitante,
			List.of(primeiroElegivel, inelegivel, melhorElegivel, segundoElegivel),
			elegivel,
			2
		);

		assertEquals(List.of("primeiro", "melhor", "segundo"), strategy.candidatosCalculados());
		assertEquals(2, resultados.size());
		assertEquals(90, resultados.get(0).percentual());
		assertEquals("compatibilidade de melhor", resultados.get(0).justificativa());
		assertEquals(70, resultados.get(1).percentual());
		assertEquals("compatibilidade de segundo", resultados.get(1).justificativa());
	}

	private record FakePerfil(String nome, int percentual, boolean elegivel) implements Perfil {

		@Override
		public String tipoPerfil() {
			return "fake";
		}
	}

	private static class FakeStrategy implements CompatibilidadeStrategy<FakePerfil> {

		private final List<String> candidatosCalculados = new ArrayList<>();

		@Override
		public ResultadoCompatibilidade calcular(FakePerfil solicitante, FakePerfil candidato) {
			candidatosCalculados.add(candidato.nome());
			return new ResultadoCompatibilidade(
				candidato.percentual(),
				"compatibilidade de " + candidato.nome(),
				List.of("criterio-fake")
			);
		}

		List<String> candidatosCalculados() {
			return candidatosCalculados;
		}
	}
}
