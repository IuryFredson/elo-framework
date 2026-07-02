package com.elo.denuncia;

import com.elo.oferta.Oferta;
import com.elo.persistencia.RepositorioBase;
import com.elo.usuario.Usuario;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public abstract class DenunciaService<D extends Denuncia, O extends Oferta, U extends Usuario, C, R> {

    @Transactional(readOnly = true)
    public List<R> listarTodas() {
        return repositorio().findAll().stream()
                .map(this::mapear)
                .toList();
    }

    @Transactional
    public R criar(C dto) {
        U denunciante = buscarDenunciante(denuncianteId(dto));
        O oferta = buscarOferta(ofertaId(dto));
        LocalDateTime agora = LocalDateTime.now();

        D denuncia = construirDenuncia(dto);
        aplicarCriacao(
                denuncia,
                denunciante,
                oferta,
                dto,
                criterio(dto),
                StatusDenuncia.PENDENTE,
                agora);

        return mapear(repositorio().save(denuncia));
    }

    @Transactional
    public R atualizarStatus(UUID id, StatusDenuncia novoStatus) {
        D denuncia = buscarEntidadePorId(id);
        StatusDenuncia statusAtual = denuncia.getStatus();

        if (!transicaoValida(statusAtual, novoStatus)) {
            throw erroTransicaoInvalida(statusAtual, novoStatus);
        }

        aplicarStatus(denuncia, novoStatus, LocalDateTime.now());
        return mapear(repositorio().save(denuncia));
    }

    @Transactional
    public void deletar(UUID id) {
        repositorio().delete(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public D buscarEntidadePorId(UUID id) {
        return repositorio().findById(id)
                .orElseThrow(() -> erroDenunciaNaoEncontrada(id));
    }

    @Transactional(readOnly = true)
    public R buscarPorId(UUID id) {
        return mapear(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<R> buscarPorOfertaId(UUID ofertaId) {
        O oferta = buscarOferta(ofertaId);
        return buscarPorOferta(oferta).stream()
                .map(this::mapear)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<R> buscarPorUsuarioId(UUID usuarioId) {
        U denunciante = buscarDenunciante(usuarioId);
        return buscarPorDenunciante(denunciante).stream()
                .map(this::mapear)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<R> buscarPorStatus(StatusDenuncia status) {
        return buscarPorStatusDenuncia(status).stream()
                .map(this::mapear)
                .toList();
    }

    protected abstract RepositorioBase<D, UUID> repositorio();

    protected abstract UUID denuncianteId(C dto);

    protected abstract UUID ofertaId(C dto);

    protected abstract U buscarDenunciante(UUID denuncianteId);

    protected abstract O buscarOferta(UUID ofertaId);

    protected abstract D construirDenuncia(C dto);

    protected abstract CriterioDenuncia criterio(C dto);

    protected abstract void aplicarCriacao(
            D denuncia,
            U denunciante,
            O oferta,
            C dto,
            CriterioDenuncia criterio,
            StatusDenuncia status,
            LocalDateTime criadoEm);

    protected abstract void aplicarStatus(D denuncia, StatusDenuncia status, LocalDateTime atualizadoEm);

    protected abstract List<D> buscarPorOferta(O oferta);

    protected abstract List<D> buscarPorDenunciante(U denunciante);

    protected abstract List<D> buscarPorStatusDenuncia(StatusDenuncia status);

    protected abstract R mapear(D denuncia);

    protected abstract RuntimeException erroDenunciaNaoEncontrada(UUID id);

    protected abstract RuntimeException erroTransicaoInvalida(StatusDenuncia atual, StatusDenuncia novo);

    protected boolean transicaoValida(StatusDenuncia atual, StatusDenuncia novo) {
        return switch (atual) {
            case PENDENTE -> novo == StatusDenuncia.EM_ANALISE;
            case EM_ANALISE -> novo == StatusDenuncia.PROCEDENTE || novo == StatusDenuncia.IMPROCEDENTE;
            case PROCEDENTE, IMPROCEDENTE -> novo == StatusDenuncia.ARQUIVADA;
            case ARQUIVADA -> false;
        };
    }
}
