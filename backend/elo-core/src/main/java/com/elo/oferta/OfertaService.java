package com.elo.oferta;

import com.elo.persistencia.RepositorioBase;
import com.elo.porta.MapperResposta;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public abstract class OfertaService<O extends Oferta, C, A, R, S> {

    @Transactional
    public R criar(C dto) {
        O oferta = construirOferta(dto);
        return mapear(repositorio().save(oferta));
    }

    @Transactional(readOnly = true)
    public List<R> listarTodos() {
        return repositorio().findAll().stream()
                .map(this::mapear)
                .toList();
    }

    @Transactional(readOnly = true)
    public R buscarPorId(UUID id) {
        return mapear(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public O buscarEntidadePorId(UUID id) {
        return repositorio().findById(id)
                .orElseThrow(() -> erroOfertaNaoEncontrada(id));
    }

    @Transactional
    public R atualizar(UUID id, UUID publicadorId, A dto) {
        O oferta = buscarEntidadePorId(id);
        validarAtualizacao(oferta, publicadorId, dto);
        aplicarAtualizacao(oferta, dto);
        return mapear(repositorio().save(oferta));
    }

    @Transactional
    public R atualizarStatus(UUID id, S novoStatus) {
        O oferta = buscarEntidadePorId(id);
        S statusAnterior = obterStatus(oferta);
        validarAlteracaoStatus(oferta, statusAnterior, novoStatus);
        aplicarStatus(oferta, novoStatus);

        O salva = repositorio().save(oferta);
        aposAlterarStatus(salva, statusAnterior, novoStatus);
        return mapear(salva);
    }

    @Transactional
    public void deletar(UUID id) {
        O oferta = buscarEntidadePorId(id);

        if (deveExcluirFisicamente(oferta)) {
            repositorio().delete(oferta);
            aposExcluirFisicamente(oferta);
            return;
        }

        S statusAnterior = obterStatus(oferta);
        aplicarExclusaoLogica(oferta);
        O salva = repositorio().save(oferta);
        aposExcluirLogicamente(salva, statusAnterior, obterStatus(salva));
    }

    protected abstract RepositorioBase<O, UUID> repositorio();

    protected abstract MapperResposta<O, R> mapperResposta();

    protected abstract RuntimeException erroOfertaNaoEncontrada(UUID id);

    protected abstract O construirOferta(C dto);

    protected abstract void validarAtualizacao(O oferta, UUID publicadorId, A dto);

    protected abstract void aplicarAtualizacao(O oferta, A dto);

    protected abstract S obterStatus(O oferta);

    protected void validarAlteracaoStatus(O oferta, S statusAnterior, S novoStatus) {
    }

    protected abstract void aplicarStatus(O oferta, S status);

    protected void aposAlterarStatus(O oferta, S statusAnterior, S novoStatus) {
    }

    protected abstract boolean deveExcluirFisicamente(O oferta);

    protected abstract void aplicarExclusaoLogica(O oferta);

    protected void aposExcluirFisicamente(O oferta) {
    }

    protected void aposExcluirLogicamente(O oferta, S statusAnterior, S novoStatus) {
    }

    private R mapear(O oferta) {
        return mapperResposta().paraResposta(oferta);
    }
}
