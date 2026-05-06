package com.apto.repository;

import com.apto.model.entity.Locador;
import com.apto.model.entity.ReputacaoLocador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReputacaoLocadorRepository extends JpaRepository<ReputacaoLocador, UUID> {
    Optional<ReputacaoLocador> findReputacaoLocadorByLocador(Locador locador);
}
