package com.elo.denuncia;

import java.util.UUID;

/**
 * Registro fixo de uma denúncia feita por um usuário contra uma oferta.
 */
public interface Denuncia {

	UUID getId();

	UUID getDenuncianteId();

	UUID getOfertaId();

	StatusDenuncia getStatus();

	String getCriterioCodigo();
}
