package com.mentormatch.repository;

import com.elo.persistencia.RepositorioBase;
import com.elo.usuario.Usuario;
import java.util.UUID;

public interface UsuarioRepository extends RepositorioBase<Usuario, UUID> {
    boolean existsByEmail(String email);
}
