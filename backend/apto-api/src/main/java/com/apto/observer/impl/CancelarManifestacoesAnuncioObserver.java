package com.apto.observer.impl;

import com.apto.event.AnuncioIndisponibilizadoEvent;
import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.enums.StatusManifestacaoInteresse;
import com.apto.observer.DomainObserver;
import com.apto.repository.ManifestacaoInteresseRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(2)
public class CancelarManifestacoesAnuncioObserver implements DomainObserver<AnuncioIndisponibilizadoEvent> {

    private final ManifestacaoInteresseRepository manifestacaoRepository;

    public CancelarManifestacoesAnuncioObserver(ManifestacaoInteresseRepository manifestacaoRepository) {
        this.manifestacaoRepository = manifestacaoRepository;
    }

    @Override
    public Class<AnuncioIndisponibilizadoEvent> eventType() {
        return AnuncioIndisponibilizadoEvent.class;
    }

    @Override
    public void update(AnuncioIndisponibilizadoEvent event) {
        List<ManifestacaoInteresse> pendentes = manifestacaoRepository
                .findByAnuncio_IdAndStatus(event.anuncioId(), StatusManifestacaoInteresse.PENDENTE);

        LocalDateTime dataResposta = LocalDateTime.now();
        pendentes.forEach(manifestacao -> {
            manifestacao.setStatus(StatusManifestacaoInteresse.CANCELADA);
            manifestacao.setDataResposta(dataResposta);
        });

        manifestacaoRepository.saveAll(pendentes);
    }
}
