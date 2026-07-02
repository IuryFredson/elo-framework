package com.studybuddy.repository;

import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.elo.persistencia.RepositorioBase;
import com.studybuddy.model.entity.ManifestacaoInteresseGrupo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ManifestacaoInteresseGrupoRepository extends RepositorioBase<ManifestacaoInteresseGrupo, UUID> {

    boolean existsByGrupo_IdAndInteressado_IdAndStatusIn(
            UUID grupoId,
            UUID interessadoId,
            Collection<StatusManifestacaoInteresse> statuses);

    List<ManifestacaoInteresseGrupo> findByGrupo_IdOrderByDataManifestacaoDesc(UUID grupoId);

    List<ManifestacaoInteresseGrupo> findByInteressado_IdOrderByDataManifestacaoDesc(UUID interessadoId);

    List<ManifestacaoInteresseGrupo> findByGrupo_IdAndStatus(
            UUID grupoId,
            StatusManifestacaoInteresse status);
}
