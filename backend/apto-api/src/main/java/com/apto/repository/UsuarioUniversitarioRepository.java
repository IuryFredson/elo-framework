package com.apto.repository;

import com.apto.model.entity.UsuarioUniversitario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UsuarioUniversitarioRepository extends JpaRepository<UsuarioUniversitario, UUID> {

    boolean existsByEmailInstitucional(String emailInstitucional);

    @Query("""
        SELECT u FROM UsuarioUniversitario u
        WHERE u.perfilConvivencia IS NOT NULL
          AND u.ativo = true
          AND u.id <> :solicitanteId
    """)
    List<UsuarioUniversitario> buscarCandidatosMatchmaking(@Param("solicitanteId") UUID solicitanteId);
}
