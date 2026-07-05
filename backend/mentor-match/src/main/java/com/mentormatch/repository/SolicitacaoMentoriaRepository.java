package com.mentormatch.repository;

import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.elo.persistencia.RepositorioBase;
import com.mentormatch.model.entity.SolicitacaoMentoria;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SolicitacaoMentoriaRepository extends RepositorioBase<SolicitacaoMentoria, UUID> {
    boolean existsBySessao_IdAndInteressado_IdAndStatusIn(UUID sessaoId, UUID alunoId, Collection<StatusManifestacaoInteresse> status);
    List<SolicitacaoMentoria> findBySessao_IdOrderByDataSolicitacaoDesc(UUID sessaoId);
    List<SolicitacaoMentoria> findByInteressado_IdOrderByDataSolicitacaoDesc(UUID alunoId);
    List<SolicitacaoMentoria> findBySessao_IdAndStatus(UUID sessaoId, StatusManifestacaoInteresse status);
}
