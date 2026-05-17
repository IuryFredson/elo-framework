package com.apto.repository;

import com.apto.model.entity.Locador;
import com.apto.model.entity.ReputacaoAnunciante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReputacaoAnuncianteRepository extends JpaRepository<ReputacaoAnunciante, UUID> {
    Optional<ReputacaoAnunciante> findReputacaoLocadorByLocador(Locador locador);
}
