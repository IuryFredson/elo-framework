package com.mentormatch.model.entity;

import com.elo.denuncia.StatusDenuncia;
import com.mentormatch.model.enums.CriterioDenunciaMentorMatch;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "denuncias_sessoes_mentoria")
@Getter @Setter @NoArgsConstructor
public class DenunciaSessaoMentoria implements com.elo.denuncia.Denuncia {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "denunciante_id") private ParticipanteMentoria denunciante;
    @ManyToOne(optional = false) @JoinColumn(name = "sessao_id") private SessaoMentoria sessao;
    private String titulo;
    @Column(length = 1000) private String corpo;
    @Enumerated(EnumType.STRING) private CriterioDenunciaMentorMatch criterio;
    @Enumerated(EnumType.STRING) private StatusDenuncia statusDenuncia;
    private LocalDateTime criadoEm;
    private LocalDateTime statusAtualizadoEm;

    @Override public UUID getDenuncianteId() { return denunciante == null ? null : denunciante.getId(); }
    @Override public UUID getOfertaId() { return sessao == null ? null : sessao.getId(); }
    @Override public StatusDenuncia getStatus() { return statusDenuncia; }
    @Override public String getCriterioCodigo() { return criterio == null ? null : criterio.codigo(); }
}
