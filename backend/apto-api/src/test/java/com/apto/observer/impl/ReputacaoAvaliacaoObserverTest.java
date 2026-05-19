package com.apto.observer.impl;

import com.apto.event.AvaliacaoAlteradaEvent;
import com.apto.event.TipoOperacaoAvaliacao;
import com.apto.service.ReputacaoCalculoService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReputacaoAvaliacaoObserverTest {

    @Test
    void deveRecalcularReputacaoQuandoAvaliacaoForAlterada() {
        ReputacaoCalculoService reputacaoCalculoService = mock(ReputacaoCalculoService.class);
        ReputacaoAvaliacaoObserver observer = new ReputacaoAvaliacaoObserver(reputacaoCalculoService);
        UUID perfilAnuncianteId = UUID.randomUUID();

        observer.update(new AvaliacaoAlteradaEvent(
                UUID.randomUUID(),
                perfilAnuncianteId,
                TipoOperacaoAvaliacao.CRIADA));

        assertEquals(AvaliacaoAlteradaEvent.class, observer.eventType());
        verify(reputacaoCalculoService).calcularReputacaoEAtualizar(perfilAnuncianteId);
    }
}
