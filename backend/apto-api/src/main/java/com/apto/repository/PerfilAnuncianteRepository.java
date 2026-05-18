package com.apto.repository;

import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PerfilAnuncianteRepository extends JpaRepository<PerfilAnunciante, UUID> {

    Optional<PerfilAnunciante> findByUsuario(Usuario usuario);

    Optional<PerfilAnunciante> findByUsuario_Id(UUID usuarioId);

    boolean existsByUsuario_Id(UUID usuarioId);
}