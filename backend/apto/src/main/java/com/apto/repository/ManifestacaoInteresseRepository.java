package com.apto.repository;

import com.apto.model.entity.ManifestacaoInteresse;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ManifestacaoInteresseRepository extends JpaRepository<ManifestacaoInteresse, UUID> {

    boolean existsByAnuncio_IdAndInteressado_IdAndStatusIn(
            UUID anuncioId,
            UUID interessadoId,
            Collection<StatusManifestacaoInteresse> statuses);

    boolean existsByAnuncio_Id(UUID anuncioId);

    List<ManifestacaoInteresse> findByAnuncio_IdOrderByDataManifestacaoDesc(UUID anuncioId);

    List<ManifestacaoInteresse> findByInteressado_IdOrderByDataManifestacaoDesc(UUID interessadoId);

    List<ManifestacaoInteresse> findByAnuncio_IdAndStatus(
            UUID anuncioId,
            StatusManifestacaoInteresse status);

    List<ManifestacaoInteresse> findByAnuncio_IdAndStatusIn(
            UUID anuncioId,
            Collection<StatusManifestacaoInteresse> statuses);
}
