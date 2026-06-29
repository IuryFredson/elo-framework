package com.elo.perfil;

import com.elo.porta.MapperResposta;
import com.elo.persistencia.RepositorioBase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public abstract class PerfilService<U, P extends Perfil, A, R> {

    @Transactional(readOnly = true)
    public R buscarPerfil(UUID usuarioId) {
        U usuario = buscarDonoPerfil(usuarioId);
        return mapperResposta().paraResposta(usuario);
    }

    @Transactional
    public R atualizarPerfil(UUID usuarioId, A dto) {
        U usuario = buscarDonoPerfil(usuarioId);
        validarAtualizacao(usuario, dto);

        aplicarDadosDonoPerfil(usuario, dto);

        P perfil = obterPerfil(usuario);
        if (perfil == null) {
            perfil = criarPerfil(dto);
            associarPerfil(usuario, perfil);
        }

        aplicarDadosPerfil(perfil, dto);
        U salvo = repositorioDonoPerfil().save(usuario);
        return mapperResposta().paraResposta(salvo);
    }

    protected abstract RepositorioBase<U, UUID> repositorioDonoPerfil();

    protected abstract MapperResposta<U, R> mapperResposta();

    protected abstract RuntimeException erroDonoPerfilNaoEncontrado(UUID usuarioId);

    protected abstract P obterPerfil(U usuario);

    protected abstract P criarPerfil(A dto);

    protected abstract void associarPerfil(U usuario, P perfil);

    protected abstract void validarAtualizacao(U usuario, A dto);

    protected abstract void aplicarDadosDonoPerfil(U usuario, A dto);

    protected abstract void aplicarDadosPerfil(P perfil, A dto);

    private U buscarDonoPerfil(UUID usuarioId) {
        return repositorioDonoPerfil().findById(usuarioId)
                .orElseThrow(() -> erroDonoPerfilNaoEncontrado(usuarioId));
    }
}
