package com.studybuddy.model.entity;

import com.elo.denuncia.StatusDenuncia;
import com.studybuddy.model.enums.CriterioDenunciaStudyBuddy;
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
@Table(name = "denuncias_grupos_estudo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaGrupoEstudo implements com.elo.denuncia.Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "denunciante_id", nullable = false)
    private Estudante denunciante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_estudo_id", nullable = false)
    private GrupoEstudo grupoEstudo;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String corpo;

    @Enumerated(EnumType.STRING)
    @Column(name = "criterio_denuncia")
    private CriterioDenunciaStudyBuddy criterio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_denuncia", nullable = false)
    private StatusDenuncia statusDenuncia;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "status_atualizado_em")
    private LocalDateTime statusAtualizadoEm;

    @Override
    public UUID getDenuncianteId() {
        return denunciante == null ? null : denunciante.getId();
    }

    @Override
    public UUID getOfertaId() {
        return grupoEstudo == null ? null : grupoEstudo.getId();
    }

    @Override
    public StatusDenuncia getStatus() {
        return statusDenuncia;
    }

    @Override
    public String getCriterioCodigo() {
        return criterio == null ? null : criterio.codigo();
    }
}
