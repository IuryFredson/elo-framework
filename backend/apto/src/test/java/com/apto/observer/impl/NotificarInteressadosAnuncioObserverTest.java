package com.apto.observer.impl;

import com.apto.event.AnuncioIndisponibilizadoEvent;
import com.apto.event.MotivoIndisponibilizacaoAnuncio;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.entity.Notificacao;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.StatusAnuncio;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.apto.repository.ManifestacaoInteresseRepository;
import com.apto.repository.NotificacaoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificarInteressadosAnuncioObserverTest {

    @Test
    void deveCriarNotificacoesParaInteressadosDoAnuncioIndisponibilizado() {
        ManifestacaoInteresseRepository manifestacaoRepository = mock(ManifestacaoInteresseRepository.class);
        NotificacaoRepository notificacaoRepository = mock(NotificacaoRepository.class);
        NotificarInteressadosAnuncioObserver observer =
                new NotificarInteressadosAnuncioObserver(manifestacaoRepository, notificacaoRepository);
        UUID anuncioId = UUID.randomUUID();

        UsuarioUniversitario interessado = new UsuarioUniversitario();
        interessado.setId(UUID.randomUUID());

        Anuncio anuncio = new Anuncio();
        anuncio.setId(anuncioId);
        anuncio.setTitulo("Apartamento perto da universidade");

        ManifestacaoInteresse manifestacao = new ManifestacaoInteresse();
        manifestacao.setInteressado(interessado);
        manifestacao.setAnuncio(anuncio);
        manifestacao.setStatus(StatusManifestacaoInteresse.PENDENTE);

        when(manifestacaoRepository.findByAnuncio_IdAndStatusIn(eq(anuncioId), any(Set.class)))
                .thenReturn(List.of(manifestacao));

        observer.update(new AnuncioIndisponibilizadoEvent(
                anuncioId,
                StatusAnuncio.ATIVO,
                StatusAnuncio.ENCERRADO,
                MotivoIndisponibilizacaoAnuncio.ENCERRADO));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Notificacao>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificacaoRepository).saveAll(captor.capture());

        Notificacao notificacao = captor.getValue().get(0);
        assertEquals(interessado, notificacao.getDestinatario());
        assertEquals("Anúncio indisponível", notificacao.getTitulo());
        assertEquals("O anúncio \"Apartamento perto da universidade\" não está mais disponível.",
                notificacao.getMensagem());
        assertFalse(notificacao.isLida());
        assertNotNull(notificacao.getDataCriacao());
    }
}
