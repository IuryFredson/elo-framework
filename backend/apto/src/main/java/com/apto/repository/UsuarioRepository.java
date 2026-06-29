package com.apto.repository;

import com.elo.usuario.Usuario;
import com.elo.persistencia.RepositorioBase;

import java.util.UUID;

public interface UsuarioRepository extends RepositorioBase<Usuario, UUID> {

    boolean existsByEmail(String email);
}

