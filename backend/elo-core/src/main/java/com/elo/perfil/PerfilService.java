package com.elo.perfil;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public abstract class PerfilService<U, P extends Perfil, A, R> {

    @Transactional(readOnly = true)
    public R buscarPerfil(UUID usuarioId) {
        U usuario = buscarDonoPerfil(usuarioId);
        return mapearResposta(usuario, obterPerfil(usuario));
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
        U salvo = salvarDonoPerfil(usuario);
        return mapearResposta(salvo, obterPerfil(salvo));
    }

    protected abstract U buscarDonoPerfil(UUID usuarioId);

    protected abstract P obterPerfil(U usuario);

    protected abstract P criarPerfil(A dto);

    protected abstract void associarPerfil(U usuario, P perfil);

    protected abstract void validarAtualizacao(U usuario, A dto);

    protected abstract void aplicarDadosDonoPerfil(U usuario, A dto);

    protected abstract void aplicarDadosPerfil(P perfil, A dto);

    protected abstract U salvarDonoPerfil(U usuario);

    protected abstract R mapearResposta(U usuario, P perfil);
}
