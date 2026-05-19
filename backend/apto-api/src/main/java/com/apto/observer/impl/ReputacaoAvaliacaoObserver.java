package com.apto.observer.impl;

import com.apto.event.AvaliacaoAlteradaEvent;
import com.apto.observer.DomainObserver;
import com.apto.service.ReputacaoCalculoService;
import org.springframework.stereotype.Component;

@Component
public class ReputacaoAvaliacaoObserver implements DomainObserver<AvaliacaoAlteradaEvent> {

    private final ReputacaoCalculoService reputacaoCalculoService;

    public ReputacaoAvaliacaoObserver(ReputacaoCalculoService reputacaoCalculoService) {
        this.reputacaoCalculoService = reputacaoCalculoService;
    }

    @Override
    public Class<AvaliacaoAlteradaEvent> eventType() {
        return AvaliacaoAlteradaEvent.class;
    }

    @Override
    public void update(AvaliacaoAlteradaEvent event) {
        reputacaoCalculoService.calcularReputacaoEAtualizar(event.perfilAnuncianteId());
    }
}
