package com.apto.observer.impl;

import com.apto.event.AnuncioIndisponibilizadoEvent;
import com.apto.event.MotivoIndisponibilizacaoAnuncio;
import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.enums.StatusAnuncio;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.apto.repository.ManifestacaoInteresseRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CancelarManifestacoesAnuncioObserverTest {

    @Test
    void deveCancelarManifestacoesPendentesDoAnuncioIndisponibilizado() {
        ManifestacaoInteresseRepository manifestacaoRepository = mock(ManifestacaoInteresseRepository.class);
        CancelarManifestacoesAnuncioObserver observer =
                new CancelarManifestacoesAnuncioObserver(manifestacaoRepository);
        UUID anuncioId = UUID.randomUUID();
        ManifestacaoInteresse manifestacao = new ManifestacaoInteresse();
        manifestacao.setStatus(StatusManifestacaoInteresse.PENDENTE);

        when(manifestacaoRepository.findByAnuncio_IdAndStatus(
                anuncioId, StatusManifestacaoInteresse.PENDENTE))
                .thenReturn(List.of(manifestacao));

        observer.update(new AnuncioIndisponibilizadoEvent(
                anuncioId,
                StatusAnuncio.ATIVO,
                StatusAnuncio.PAUSADO,
                MotivoIndisponibilizacaoAnuncio.PAUSADO));

        assertEquals(StatusManifestacaoInteresse.CANCELADA, manifestacao.getStatus());
        assertNotNull(manifestacao.getDataResposta());
        verify(manifestacaoRepository).saveAll(List.of(manifestacao));
    }
}
