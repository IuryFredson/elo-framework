package com.elo.oferta;

import java.util.UUID;

/**
 * Oportunidade publicada por um usuário da plataforma.
 */
public interface Oferta {

	UUID getId();

	UUID getPublicadorId();

	String tipoOferta();

	boolean isAtiva();
}
