package com.apto.repository;

import com.apto.model.entity.Locador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocadorRepository extends JpaRepository<Locador, UUID> {

    boolean existsByDocumentoIdentificacao(String documentoIdentificacao);
}
