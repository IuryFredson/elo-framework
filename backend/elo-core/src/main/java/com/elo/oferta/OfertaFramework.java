package com.elo.oferta;

import java.util.UUID;

public interface OfertaFramework {

	UUID getId();

	UUID getPublicadorId();

	String tipoOferta();

	boolean isAtiva();
}
