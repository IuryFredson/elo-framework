package com.apto.event;

import com.apto.model.enums.StatusAnuncio;
import com.apto.observer.DomainEvent;

import java.util.UUID;

public record AnuncioIndisponibilizadoEvent(
        UUID anuncioId,
        StatusAnuncio statusAnterior,
        StatusAnuncio statusNovo,
        MotivoIndisponibilizacaoAnuncio motivo
) implements DomainEvent {
}
