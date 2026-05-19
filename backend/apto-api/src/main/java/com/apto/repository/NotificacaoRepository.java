package com.apto.repository;

import com.apto.model.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {

    List<Notificacao> findByDestinatario_IdOrderByDataCriacaoDesc(UUID destinatarioId);
}
