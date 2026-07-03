package com.studybuddy.repository;

import com.elo.denuncia.StatusDenuncia;
import com.elo.persistencia.RepositorioBase;
import com.studybuddy.model.entity.DenunciaGrupoEstudo;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.GrupoEstudo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DenunciaGrupoEstudoRepository extends RepositorioBase<DenunciaGrupoEstudo, UUID> {

    List<DenunciaGrupoEstudo> findByGrupoEstudo(GrupoEstudo grupoEstudo);

    List<DenunciaGrupoEstudo> findByDenunciante(Estudante denunciante);

    List<DenunciaGrupoEstudo> findByStatusDenuncia(StatusDenuncia statusDenuncia);
}
