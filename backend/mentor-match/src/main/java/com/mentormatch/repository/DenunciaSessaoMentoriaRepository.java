package com.mentormatch.repository;

import com.elo.denuncia.StatusDenuncia;
import com.elo.persistencia.RepositorioBase;
import com.mentormatch.model.entity.*;
import java.util.List;
import java.util.UUID;

public interface DenunciaSessaoMentoriaRepository extends RepositorioBase<DenunciaSessaoMentoria, UUID> {
    List<DenunciaSessaoMentoria> findBySessao(SessaoMentoria sessao);
    List<DenunciaSessaoMentoria> findByDenunciante(ParticipanteMentoria denunciante);
    List<DenunciaSessaoMentoria> findByStatusDenuncia(StatusDenuncia status);
}
