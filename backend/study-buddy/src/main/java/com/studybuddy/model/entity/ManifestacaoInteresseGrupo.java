package com.studybuddy.model.entity;

import com.elo.manifestacao.StatusManifestacaoInteresse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "manifestacoes_interesse_grupo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManifestacaoInteresseGrupo implements com.elo.manifestacao.ManifestacaoInteresse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_estudo_id", nullable = false)
    private GrupoEstudo grupo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "interessado_id", nullable = false)
    private Estudante interessado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusManifestacaoInteresse status;

    @Column(length = 500)
    private String mensagem;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataManifestacao;

    private LocalDateTime dataResposta;

    @Override
    public UUID getInteressadoId() {
        return interessado == null ? null : interessado.getId();
    }

    @Override
    public UUID getOfertaId() {
        return grupo == null ? null : grupo.getId();
    }
}
