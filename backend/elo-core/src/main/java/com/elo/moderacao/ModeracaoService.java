package com.elo.moderacao;

import com.elo.denuncia.Denuncia;
import com.elo.denuncia.StatusDenuncia;
import com.elo.oferta.Oferta;
import com.elo.persistencia.RepositorioBase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class ModeracaoService<D extends Denuncia, O extends Oferta, C, R> {

    @Transactional
    public R moderar(UUID denunciaId, C dto) {
        D denuncia = buscarDenunciaPorId(denunciaId);
        O oferta = ofertaDaDenuncia(denuncia);

        StatusDenuncia statusAnteriorDenuncia = denuncia.getStatus();
        Object statusAnteriorOferta = statusOferta(oferta);
        StatusDenuncia novoStatus = novoStatus(dto);
        AcaoModeracaoOferta acao = acaoOferta(dto);
        LocalDateTime moderadoEm = LocalDateTime.now();

        validarDecisao(statusAnteriorDenuncia, novoStatus, acao);
        aplicarDecisaoNaDenuncia(denuncia, novoStatus, moderadoEm);
        aplicarAcaoNaOferta(oferta, acao, novoStatus);

        repositorioOferta().save(oferta);
        repositorioDenuncia().save(denuncia);

        return mapearResposta(
                denuncia,
                oferta,
                statusAnteriorDenuncia,
                statusAnteriorOferta,
                dto,
                moderadoEm);
    }

    protected abstract RepositorioBase<D, UUID> repositorioDenuncia();

    protected abstract RepositorioBase<O, UUID> repositorioOferta();

    protected abstract O ofertaDaDenuncia(D denuncia);

    protected abstract StatusDenuncia novoStatus(C dto);

    protected abstract AcaoModeracaoOferta acaoOferta(C dto);

    protected abstract Object statusOferta(O oferta);

    protected abstract void aplicarStatusDenuncia(D denuncia, StatusDenuncia novoStatus, LocalDateTime atualizadoEm);

    protected abstract void pausarOferta(O oferta);

    protected abstract void encerrarOferta(O oferta);

    protected abstract R mapearResposta(
            D denuncia,
            O oferta,
            StatusDenuncia statusAnteriorDenuncia,
            Object statusAnteriorOferta,
            C dto,
            LocalDateTime moderadoEm);

    protected abstract RuntimeException erroDenunciaNaoEncontrada(UUID id);

    protected abstract RuntimeException erroModeracaoInvalida(String mensagem);

    protected D buscarDenunciaPorId(UUID denunciaId) {
        return repositorioDenuncia().findById(denunciaId)
                .orElseThrow(() -> erroDenunciaNaoEncontrada(denunciaId));
    }

    protected void aplicarDecisaoNaDenuncia(D denuncia, StatusDenuncia novoStatus, LocalDateTime atualizadoEm) {
        aplicarStatusDenuncia(denuncia, novoStatus, atualizadoEm);
    }

    protected void aplicarAcaoNaOferta(O oferta, AcaoModeracaoOferta acao, StatusDenuncia novoStatusDenuncia) {
        if (novoStatusDenuncia != StatusDenuncia.PROCEDENTE) {
            return;
        }

        switch (acao) {
            case NENHUMA -> {
            }
            case PAUSAR -> pausarOferta(oferta);
            case ENCERRAR -> encerrarOferta(oferta);
        }
    }

    protected void validarDecisao(
            StatusDenuncia statusAtual,
            StatusDenuncia novoStatus,
            AcaoModeracaoOferta acaoOferta) {

        if (statusAtual == StatusDenuncia.ARQUIVADA) {
            throw erroModeracaoInvalida("Não é possível moderar uma denúncia arquivada.");
        }

        switch (novoStatus) {
            case EM_ANALISE -> validarEntradaEmAnalise(statusAtual, acaoOferta);
            case IMPROCEDENTE -> validarImprocedencia(statusAtual, acaoOferta);
            case PROCEDENTE -> validarProcedencia(statusAtual, acaoOferta);
            case ARQUIVADA -> validarArquivamento(statusAtual, acaoOferta);
            case PENDENTE -> throw erroModeracaoInvalida("Não é permitido retornar denúncia para PENDENTE.");
        }
    }

    private void validarEntradaEmAnalise(StatusDenuncia statusAtual, AcaoModeracaoOferta acaoOferta) {
        if (statusAtual != StatusDenuncia.PENDENTE) {
            throw erroModeracaoInvalida("Só denúncias PENDENTES podem ser colocadas EM_ANALISE.");
        }

        if (acaoOferta != AcaoModeracaoOferta.NENHUMA) {
            throw erroModeracaoInvalida("Não é permitido aplicar ação ao anúncio ao colocar denúncia EM_ANALISE.");
        }
    }

    private void validarImprocedencia(StatusDenuncia statusAtual, AcaoModeracaoOferta acaoOferta) {
        if (statusAtual != StatusDenuncia.EM_ANALISE) {
            throw erroModeracaoInvalida("Só denúncias EM_ANALISE podem ser julgadas como IMPROCEDENTE.");
        }

        if (acaoOferta != AcaoModeracaoOferta.NENHUMA) {
            throw erroModeracaoInvalida("Denúncia IMPROCEDENTE não pode aplicar ação ao anúncio.");
        }
    }

    private void validarProcedencia(StatusDenuncia statusAtual, AcaoModeracaoOferta acaoOferta) {
        if (statusAtual != StatusDenuncia.EM_ANALISE) {
            throw erroModeracaoInvalida("Só denúncias EM_ANALISE podem ser julgadas como PROCEDENTE.");
        }

        if (acaoOferta == AcaoModeracaoOferta.NENHUMA) {
            throw erroModeracaoInvalida("Denúncia PROCEDENTE deve aplicar uma ação ao anúncio.");
        }
    }

    private void validarArquivamento(StatusDenuncia statusAtual, AcaoModeracaoOferta acaoOferta) {
        if (statusAtual != StatusDenuncia.PROCEDENTE && statusAtual != StatusDenuncia.IMPROCEDENTE) {
            throw erroModeracaoInvalida("Só denúncias PROCEDENTE ou IMPROCEDENTE podem ser arquivadas.");
        }

        if (acaoOferta != AcaoModeracaoOferta.NENHUMA) {
            throw erroModeracaoInvalida("Arquivamento não pode aplicar ação ao anúncio.");
        }
    }
}
