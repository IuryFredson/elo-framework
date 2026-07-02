package com.studybuddy.repository;

import com.elo.persistencia.RepositorioBase;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.enums.StatusGrupoEstudo;

import java.util.List;
import java.util.UUID;

public interface GrupoEstudoRepository extends RepositorioBase<GrupoEstudo, UUID> {

    List<GrupoEstudo> findByStatus(StatusGrupoEstudo status);
}
