package com.apto.repository;

import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Moradia;
import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {
    Optional<Anuncio> findByAnunciante_Usuario_Id(UUID usuarioId);
    Boolean existsByMoradia(Moradia moradia);
    Page<Anuncio> findByStatus(StatusAnuncio status, Pageable pageable);

    @Query("""
            SELECT a FROM Anuncio a JOIN a.moradia m
            WHERE a.status = 'ATIVO'
            AND (:valorMin IS NULL OR a.valorMensal >= :valorMin)
            AND (:valorMax IS NULL OR a.valorMensal <= :valorMax)
            AND (:bairro IS NULL OR LOWER(m.bairro) LIKE LOWER(CONCAT('%', CAST(:bairro AS String), '%')))
            AND (:tipoMoradia IS NULL OR m.tipoMoradia = :tipoMoradia)
            AND (:tipoAnuncio IS NULL OR a.tipoAnuncio = :tipoAnuncio)
            AND (:mobiliado IS NULL OR m.mobiliado = :mobiliado)
            AND (:aceitaAnimais IS NULL OR m.aceitaAnimais = :aceitaAnimais)
            AND (:quantidadeVagas IS NULL OR m.quantidadeVagas >= :quantidadeVagas)
            """)
    Page<Anuncio> buscarComFiltros(
            @Param("valorMin") BigDecimal valorMin,
            @Param("valorMax") BigDecimal valorMax,
            @Param("bairro") String bairro,
            @Param("tipoMoradia") TipoMoradia tipoMoradia,
            @Param("tipoAnuncio") TipoAnuncio tipoAnuncio,
            @Param("mobiliado") Boolean mobiliado,
            @Param("aceitaAnimais") Boolean aceitaAnimais,
            @Param("quantidadeVagas") Integer quantidadeVagas,
            Pageable pageable);
}
