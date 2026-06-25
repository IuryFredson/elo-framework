package com.elo.interacao;

import java.util.UUID;

public interface InteracaoFramework {

	UUID getId();

	UUID getInteressadoId();

	UUID getOfertaId();

	String tipoFixo();

	STATUS_INTERACAO getStatus();
}
