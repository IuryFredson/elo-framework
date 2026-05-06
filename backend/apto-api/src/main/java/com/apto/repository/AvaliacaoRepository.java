package com.apto.repository;

import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.Locador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {

    boolean existsByAvaliador_IdAndAnuncio_IdAndAtivaTrue(UUID avaliadorId, UUID anuncioId);

    List<Avaliacao> findByLocadorAvaliado_IdAndAtivaTrue(UUID locadorId);

    List<Avaliacao> findByMoradia_IdAndAtivaTrue(UUID moradiaId);

    List<Avaliacao> findByAvaliador_IdAndAtivaTrue(UUID avaliadorId);

    List<Avaliacao> findByLocadorAvaliadoAndAtivaTrue(Locador locador);
}
