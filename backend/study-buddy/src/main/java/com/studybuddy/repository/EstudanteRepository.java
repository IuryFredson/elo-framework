package com.studybuddy.repository;

import com.elo.persistencia.RepositorioBase;
import com.studybuddy.model.entity.Estudante;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EstudanteRepository extends RepositorioBase<Estudante, UUID> {

    boolean existsByMatricula(String matricula);

    @Query("""
        SELECT e FROM Estudante e
        WHERE e.perfilAcademico IS NOT NULL
          AND e.ativo = true
          AND e.id <> :solicitanteId
    """)
    List<Estudante> buscarCandidatosMatching(@Param("solicitanteId") UUID solicitanteId);
}
