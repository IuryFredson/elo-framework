package com.elo.compatibilidade;

import com.elo.perfil.Perfil;

public interface CompatibilidadeStrategy<P extends Perfil> {

	ResultadoCompatibilidade calcular(P solicitante, P candidato);
}
