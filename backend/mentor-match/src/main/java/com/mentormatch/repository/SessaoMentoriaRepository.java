package com.mentormatch.repository;

import com.elo.persistencia.RepositorioBase;
import com.mentormatch.model.entity.SessaoMentoria;
import com.mentormatch.model.enums.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface SessaoMentoriaRepository extends RepositorioBase<SessaoMentoria, UUID> {
    @Query("""
        SELECT s FROM SessaoMentoria s
        WHERE s.status = com.mentormatch.model.enums.StatusSessaoMentoria.ATIVA
          AND (:area IS NULL OR LOWER(s.area) LIKE LOWER(CONCAT('%', :area, '%')))
          AND (:modalidade IS NULL OR s.modalidade = :modalidade)
          AND (:nivel IS NULL OR s.nivelAtendido = :nivel)
          AND (:periodo IS NULL OR s.periodo = :periodo)
        ORDER BY s.dataPublicacao DESC
        """)
    List<SessaoMentoria> buscarAtivas(
            @Param("area") String area, @Param("modalidade") ModalidadeMentoria modalidade,
            @Param("nivel") NivelConhecimento nivel, @Param("periodo") PeriodoDisponibilidade periodo);
}
