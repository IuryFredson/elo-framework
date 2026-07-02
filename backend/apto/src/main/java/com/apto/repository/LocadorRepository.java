package com.apto.repository;

import com.apto.model.entity.Locador;
import com.elo.persistencia.RepositorioBase;
import java.util.UUID;

public interface LocadorRepository extends RepositorioBase<Locador, UUID> {

    boolean existsByDocumentoIdentificacao(String documentoIdentificacao);
}
