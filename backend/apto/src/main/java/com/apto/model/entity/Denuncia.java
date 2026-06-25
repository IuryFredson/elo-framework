package com.apto.model.entity;

import com.apto.model.enums.StatusDenuncia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "denuncia")
public class Denuncia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario denunciante;

    @ManyToOne
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String corpo;

    @Enumerated(EnumType.STRING)
    @Column(name= "status_denuncia",nullable = false)
    private StatusDenuncia statusDenuncia;

    @Column(name="criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "status_atualizado_em")
    private LocalDateTime statusAtualizadoEm;
}
