package com.elo.compatibilidade;

import com.elo.perfil.PerfilFramework;

public interface CompatibilidadeStrategy<P extends PerfilFramework> {

	ResultadoCompatibilidade calcular(P solicitante, P candidato);
}
