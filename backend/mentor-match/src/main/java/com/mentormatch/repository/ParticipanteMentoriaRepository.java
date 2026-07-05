package com.mentormatch.repository;

import com.elo.persistencia.RepositorioBase;
import com.mentormatch.model.entity.ParticipanteMentoria;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipanteMentoriaRepository extends RepositorioBase<ParticipanteMentoria, UUID> {
    @Query("""
        SELECT p FROM ParticipanteMentoria p
        WHERE TYPE(p) = Mentor
          AND p.ativo = true
          AND p.perfilMentoria IS NOT NULL
          AND p.id <> :solicitanteId
        """)
    List<ParticipanteMentoria> buscarMentoresCandidatos(@Param("solicitanteId") UUID solicitanteId);
}
