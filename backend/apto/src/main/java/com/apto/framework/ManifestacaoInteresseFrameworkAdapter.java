package com.apto.framework;

import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.enums.StatusManifestacaoInteresse;
import com.elo.interacao.InteracaoFramework;
import com.elo.interacao.STATUS_INTERACAO;

import java.util.Objects;
import java.util.UUID;

public class ManifestacaoInteresseFrameworkAdapter implements InteracaoFramework {

    private final ManifestacaoInteresse manifestacaoInteresse;

    public ManifestacaoInteresseFrameworkAdapter(ManifestacaoInteresse manifestacaoInteresse) {
        this.manifestacaoInteresse = Objects.requireNonNull(manifestacaoInteresse, "manifestacaoInteresse must not be null");
    }

    @Override
    public UUID getId() {
        return manifestacaoInteresse.getId();
    }

    @Override
    public UUID getInteressadoId() {
        return manifestacaoInteresse.getInteressadoId();
    }

    @Override
    public UUID getOfertaId() {
        return manifestacaoInteresse.getOfertaId();
    }

    @Override
    public String tipoFixo() {
        return manifestacaoInteresse.tipoFixo();
    }

    @Override
    public STATUS_INTERACAO getStatus() {
        StatusManifestacaoInteresse status = manifestacaoInteresse.getStatus();
        if (status == null) {
            return null;
        }

        return switch (status) {
            case PENDENTE, ACEITA -> STATUS_INTERACAO.ATIVO;
            case RECUSADA -> STATUS_INTERACAO.ENCERRADO;
            case CANCELADA -> STATUS_INTERACAO.PAUSADO;
        };
    }
}
