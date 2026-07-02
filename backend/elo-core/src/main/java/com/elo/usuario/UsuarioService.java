package com.elo.usuario;

import com.elo.porta.MapperResposta;
import com.elo.persistencia.RepositorioBase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public abstract class UsuarioService<T extends Usuario, C, A, R> {

    @Transactional
    public R criar(C dto) {
        validarEmailDisponivel(emailCriacao(dto));
        validarCriacaoEspecifica(dto);

        T usuario = construirEntidade(dto);
        aplicarDadosComunsCriacao(usuario, dto);
        aplicarDadosEspecificosCriacao(usuario, dto);

        T salvo = repositorio().save(usuario);
        aposCriar(salvo, dto);
        return mapperResposta().paraResposta(salvo);
    }

    @Transactional(readOnly = true)
    public List<R> listarTodos() {
        return repositorio().findAll().stream()
                .map(mapperResposta()::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public R buscarPorId(UUID id) {
        return mapperResposta().paraResposta(buscarEntidadePorId(id));
    }

    @Transactional
    public R atualizar(UUID id, A dto) {
        T usuario = buscarEntidadePorId(id);
        validarEmailAtualizacao(usuario, emailAtualizacao(dto));
        validarAtualizacaoEspecifica(usuario, dto);

        aplicarDadosComunsAtualizacao(usuario, dto);
        aplicarDadosEspecificosAtualizacao(usuario, dto);

        return mapperResposta().paraResposta(repositorio().save(usuario));
    }

    @Transactional
    public R alterarStatus(UUID id, boolean ativo) {
        T usuario = buscarEntidadePorId(id);
        usuario.setAtivo(ativo);
        return mapperResposta().paraResposta(repositorio().save(usuario));
    }

    @Transactional
    public R ativar(UUID id) {
        return alterarStatus(id, true);
    }

    @Transactional
    public R inativar(UUID id) {
        return alterarStatus(id, false);
    }

    @Transactional
    public void deletar(UUID id) {
        repositorio().delete(buscarEntidadePorId(id));
    }

    protected abstract T construirEntidade(C dto);

    protected abstract RepositorioBase<T, UUID> repositorio();

    protected abstract MapperResposta<T, R> mapperResposta();

    protected abstract RuntimeException erroUsuarioNaoEncontrado(UUID id);

    protected abstract boolean existeEmail(String email);

    protected abstract RuntimeException erroEmailDuplicado(String email);

    protected abstract String nomeCriacao(C dto);

    protected abstract String emailCriacao(C dto);

    protected abstract String telefoneCriacao(C dto);

    protected abstract String nomeAtualizacao(A dto);

    protected abstract String emailAtualizacao(A dto);

    protected abstract String telefoneAtualizacao(A dto);

    protected void validarCriacaoEspecifica(C dto) {
    }

    protected void validarAtualizacaoEspecifica(T usuario, A dto) {
    }

    protected void aplicarDadosEspecificosCriacao(T usuario, C dto) {
    }

    protected void aplicarDadosEspecificosAtualizacao(T usuario, A dto) {
    }

    protected void aposCriar(T usuario, C dto) {
    }

    private T buscarEntidadePorId(UUID id) {
        return repositorio().findById(id)
                .orElseThrow(() -> erroUsuarioNaoEncontrado(id));
    }

    private void validarEmailDisponivel(String email) {
        if (existeEmail(email)) {
            throw erroEmailDuplicado(email);
        }
    }

    private void validarEmailAtualizacao(T usuario, String novoEmail) {
        if (!usuario.getEmail().equals(novoEmail) && existeEmail(novoEmail)) {
            throw erroEmailDuplicado(novoEmail);
        }
    }

    private void aplicarDadosComunsCriacao(T usuario, C dto) {
        usuario.setNome(nomeCriacao(dto));
        usuario.setEmail(emailCriacao(dto));
        usuario.setTelefone(telefoneCriacao(dto));
        usuario.setAtivo(true);
    }

    private void aplicarDadosComunsAtualizacao(T usuario, A dto) {
        usuario.setNome(nomeAtualizacao(dto));
        usuario.setEmail(emailAtualizacao(dto));
        usuario.setTelefone(telefoneAtualizacao(dto));
    }
}
