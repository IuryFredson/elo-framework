package com.elo.arquitetura;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DependenciaArquiteturalTest {

	@Test
	void eloCoreNaoDeveDependerDePacotesDoApto() throws IOException {
		Path fontesDoCore = Path.of("src", "main", "java");

		try (var arquivos = Files.walk(fontesDoCore)) {
			List<Path> violacoes = arquivos
				.filter(path -> path.toString().endsWith(".java"))
				.filter(this::referenciaPacoteDoApto)
				.toList();

			assertTrue(violacoes.isEmpty(),
				() -> "elo-core não pode depender de com.apto. Violações: " + violacoes);
		}
	}

	private boolean referenciaPacoteDoApto(Path arquivo) {
		try {
			return Files.readString(arquivo).contains("com.apto");
		} catch (IOException exception) {
			throw new IllegalStateException("Não foi possível inspecionar " + arquivo, exception);
		}
	}
}
