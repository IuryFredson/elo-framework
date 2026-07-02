package com.elo.manifestacao;

import java.util.UUID;

/**
 * Interação fixa entre um usuário interessado e uma oferta.
 */
public interface ManifestacaoInteresse {

	UUID getId();

	UUID getInteressadoId();

	UUID getOfertaId();

	StatusManifestacaoInteresse getStatus();
}
