package com.studybuddy.repository;

import com.elo.persistencia.RepositorioBase;
import com.studybuddy.model.entity.Estudante;

import java.util.UUID;

public interface EstudanteRepository extends RepositorioBase<Estudante, UUID> {

    boolean existsByMatricula(String matricula);
}
