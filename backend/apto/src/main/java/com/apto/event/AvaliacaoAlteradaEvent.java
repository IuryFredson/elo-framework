package com.apto.event;

import com.apto.observer.DomainEvent;

import java.util.UUID;

public record AvaliacaoAlteradaEvent(
        UUID avaliacaoId,
        UUID perfilAnuncianteId,
        TipoOperacaoAvaliacao tipoOperacao
) implements DomainEvent {
}
