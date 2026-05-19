package com.apto.observer.impl;

import com.apto.event.AnuncioIndisponibilizadoEvent;
import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.entity.Notificacao;
import com.apto.model.enums.StatusManifestacaoInteresse;
import com.apto.observer.DomainObserver;
import com.apto.repository.ManifestacaoInteresseRepository;
import com.apto.repository.NotificacaoRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@Order(1)
public class NotificarInteressadosAnuncioObserver implements DomainObserver<AnuncioIndisponibilizadoEvent> {

    private static final Set<StatusManifestacaoInteresse> STATUS_NOTIFICAVEIS =
            Set.of(StatusManifestacaoInteresse.PENDENTE, StatusManifestacaoInteresse.ACEITA);

    private final ManifestacaoInteresseRepository manifestacaoRepository;
    private final NotificacaoRepository notificacaoRepository;

    public NotificarInteressadosAnuncioObserver(ManifestacaoInteresseRepository manifestacaoRepository,
                                                NotificacaoRepository notificacaoRepository) {
        this.manifestacaoRepository = manifestacaoRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    @Override
    public Class<AnuncioIndisponibilizadoEvent> eventType() {
        return AnuncioIndisponibilizadoEvent.class;
    }

    @Override
    public void update(AnuncioIndisponibilizadoEvent event) {
        List<ManifestacaoInteresse> manifestacoes = manifestacaoRepository
                .findByAnuncio_IdAndStatusIn(event.anuncioId(), STATUS_NOTIFICAVEIS);

        LocalDateTime dataCriacao = LocalDateTime.now();
        List<Notificacao> notificacoes = manifestacoes.stream()
                .map(manifestacao -> criarNotificacao(manifestacao, dataCriacao))
                .toList();

        notificacaoRepository.saveAll(notificacoes);
    }

    private Notificacao criarNotificacao(ManifestacaoInteresse manifestacao, LocalDateTime dataCriacao) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDestinatario(manifestacao.getInteressado());
        notificacao.setTitulo("Anúncio indisponível");
        notificacao.setMensagem("O anúncio \"" + manifestacao.getAnuncio().getTitulo()
                + "\" não está mais disponível.");
        notificacao.setLida(false);
        notificacao.setDataCriacao(dataCriacao);
        return notificacao;
    }
}
