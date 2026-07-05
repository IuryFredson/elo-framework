package com.mentormatch.model.entity;

import com.elo.manifestacao.StatusManifestacaoInteresse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "solicitacoes_mentoria")
@Getter @Setter @NoArgsConstructor
public class SolicitacaoMentoria implements com.elo.manifestacao.ManifestacaoInteresse {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "sessao_id") private SessaoMentoria sessao;
    @ManyToOne(optional = false) @JoinColumn(name = "aluno_id") private Aluno interessado;
    @Enumerated(EnumType.STRING) private StatusManifestacaoInteresse status;
    @Column(length = 500) private String mensagem;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataResposta;

    @Override public UUID getInteressadoId() { return interessado == null ? null : interessado.getId(); }
    @Override public UUID getOfertaId() { return sessao == null ? null : sessao.getId(); }
}
