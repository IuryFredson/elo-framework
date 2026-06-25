package com.elo.compatibilidade;

import java.util.List;

public record ResultadoCompatibilidade(
	int percentual,
	String justificativa,
	List<String> criteriosAtendidos
) {
}
