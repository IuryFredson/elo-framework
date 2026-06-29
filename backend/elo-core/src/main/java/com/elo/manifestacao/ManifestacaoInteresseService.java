package com.elo.manifestacao;

import com.elo.oferta.Oferta;
import com.elo.persistencia.RepositorioBase;
import com.elo.usuario.Usuario;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class ManifestacaoInteresseService<M extends ManifestacaoInteresse, O extends Oferta, U extends Usuario, C, R, D> {

    private static final Set<StatusManifestacaoInteresse> STATUS_ATIVOS =
            Set.of(StatusManifestacaoInteresse.PENDENTE, StatusManifestacaoInteresse.ACEITA);

    @Transactional
    public D criar(C dto) {
        O oferta = buscarOferta(ofertaId(dto));
        U interessado = buscarInteressado(interessadoId(dto));

        validarOfertaAtiva(oferta);
        validarInteresseProprio(oferta, interessado);
        validarDuplicidade(oferta, interessado);

        M manifestacao = construirManifestacao(oferta, interessado, dto);
        aplicarCriacao(
                manifestacao,
                oferta,
                interessado,
                dto,
                StatusManifestacaoInteresse.PENDENTE,
                LocalDateTime.now());

        return mapearDetalhe(repositorio().save(manifestacao));
    }

    @Transactional
    public D aceitar(UUID id, UUID publicadorId) {
        M manifestacao = buscarEntidadePorId(id);
        validarPublicador(manifestacao, publicadorId);
        validarTransicaoDePendente(manifestacao);
        aplicarResposta(manifestacao, StatusManifestacaoInteresse.ACEITA, LocalDateTime.now());
        return mapearDetalhe(repositorio().save(manifestacao));
    }

    @Transactional
    public D recusar(UUID id, UUID publicadorId) {
        M manifestacao = buscarEntidadePorId(id);
        validarPublicador(manifestacao, publicadorId);
        validarTransicaoDePendente(manifestacao);
        aplicarResposta(manifestacao, StatusManifestacaoInteresse.RECUSADA, LocalDateTime.now());
        return mapearDetalhe(repositorio().save(manifestacao));
    }

    @Transactional
    public D cancelar(UUID id, UUID interessadoId) {
        M manifestacao = buscarEntidadePorId(id);
        validarInteressado(manifestacao, interessadoId);
        validarTransicaoDePendente(manifestacao);
        aplicarResposta(manifestacao, StatusManifestacaoInteresse.CANCELADA, LocalDateTime.now());
        return mapearDetalhe(repositorio().save(manifestacao));
    }

    @Transactional(readOnly = true)
    public List<R> listarPorOferta(UUID ofertaId, UUID publicadorId) {
        O oferta = buscarOferta(ofertaId);
        if (!publicadorId.equals(oferta.getPublicadorId())) {
            throw erroAcessoNegadoListagemOferta();
        }

        return buscarPorOfertaOrdenado(ofertaId).stream()
                .map(this::mapearResumo)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<R> listarPorInteressado(UUID interessadoId) {
        return buscarPorInteressadoOrdenado(interessadoId).stream()
                .map(this::mapearResumo)
                .toList();
    }

    @Transactional(readOnly = true)
    public D buscarPorId(UUID id, UUID solicitanteId) {
        M manifestacao = buscarEntidadePorId(id);
        if (!solicitanteId.equals(manifestacao.getInteressadoId())
                && !solicitanteId.equals(publicadorId(manifestacao))) {
            throw erroAcessoNegadoVisualizacao();
        }
        return mapearDetalhe(manifestacao);
    }

    @Transactional
    public void cancelarPendentesDaOferta(UUID ofertaId) {
        List<M> pendentes = buscarPorOfertaEStatus(ofertaId, StatusManifestacaoInteresse.PENDENTE);
        pendentes.forEach(manifestacao ->
                aplicarResposta(manifestacao, StatusManifestacaoInteresse.CANCELADA, LocalDateTime.now()));
        repositorio().saveAll(pendentes);
    }

    protected abstract RepositorioBase<M, UUID> repositorio();

    protected abstract UUID ofertaId(C dto);

    protected abstract UUID interessadoId(C dto);

    protected abstract O buscarOferta(UUID ofertaId);

    protected abstract U buscarInteressado(UUID interessadoId);

    protected abstract M construirManifestacao(O oferta, U interessado, C dto);

    protected abstract void aplicarCriacao(
            M manifestacao,
            O oferta,
            U interessado,
            C dto,
            StatusManifestacaoInteresse status,
            LocalDateTime dataManifestacao);

    protected abstract void aplicarResposta(
            M manifestacao,
            StatusManifestacaoInteresse status,
            LocalDateTime dataResposta);

    protected abstract boolean existeManifestacaoAtiva(
            UUID ofertaId,
            UUID interessadoId,
            Collection<StatusManifestacaoInteresse> statusAtivos);

    protected abstract List<M> buscarPorOfertaOrdenado(UUID ofertaId);

    protected abstract List<M> buscarPorInteressadoOrdenado(UUID interessadoId);

    protected abstract List<M> buscarPorOfertaEStatus(
            UUID ofertaId,
            StatusManifestacaoInteresse status);

    protected abstract UUID publicadorId(M manifestacao);

    protected abstract R mapearResumo(M manifestacao);

    protected abstract D mapearDetalhe(M manifestacao);

    protected abstract RuntimeException erroManifestacaoNaoEncontrada(UUID id);

    protected abstract RuntimeException erroOfertaInativa();

    protected abstract RuntimeException erroInteresseProprio();

    protected abstract RuntimeException erroManifestacaoDuplicada();

    protected abstract RuntimeException erroAcessoNegado();

    protected abstract RuntimeException erroTransicaoInvalida();

    protected RuntimeException erroAcessoNegadoResposta() {
        return erroAcessoNegado();
    }

    protected RuntimeException erroAcessoNegadoCancelamento() {
        return erroAcessoNegado();
    }

    protected RuntimeException erroAcessoNegadoListagemOferta() {
        return erroAcessoNegado();
    }

    protected RuntimeException erroAcessoNegadoVisualizacao() {
        return erroAcessoNegado();
    }

    protected M buscarEntidadePorId(UUID id) {
        return repositorio().findById(id)
                .orElseThrow(() -> erroManifestacaoNaoEncontrada(id));
    }

    private void validarOfertaAtiva(O oferta) {
        if (!oferta.isAtiva()) {
            throw erroOfertaInativa();
        }
    }

    private void validarInteresseProprio(O oferta, U interessado) {
        if (oferta.getPublicadorId().equals(interessado.getId())) {
            throw erroInteresseProprio();
        }
    }

    private void validarDuplicidade(O oferta, U interessado) {
        if (existeManifestacaoAtiva(oferta.getId(), interessado.getId(), STATUS_ATIVOS)) {
            throw erroManifestacaoDuplicada();
        }
    }

    private void validarPublicador(M manifestacao, UUID publicadorId) {
        if (!publicadorId.equals(publicadorId(manifestacao))) {
            throw erroAcessoNegadoResposta();
        }
    }

    private void validarInteressado(M manifestacao, UUID interessadoId) {
        if (!interessadoId.equals(manifestacao.getInteressadoId())) {
            throw erroAcessoNegadoCancelamento();
        }
    }

    private void validarTransicaoDePendente(M manifestacao) {
        if (manifestacao.getStatus() != StatusManifestacaoInteresse.PENDENTE) {
            throw erroTransicaoInvalida();
        }
    }
}
