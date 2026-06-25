package com.apto.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "avaliacoes")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "avaliador_id", nullable = false)
    private UsuarioUniversitario avaliador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "anunciante_avaliado_id", nullable = false)
    private PerfilAnunciante anuncianteAvaliado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "moradia_id", nullable = false)
    private Moradia moradia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    @Column(name = "nota_geral", nullable = false)
    private Integer notaGeral;

    @Column(name = "nota_comunicacao", nullable = false)
    private Integer notaComunicacao;

    @Column(name = "nota_fidelidade_anuncio", nullable = false)
    private Integer notaFidelidadeAnuncio;

    @Column(name = "nota_estado_moradia", nullable = false)
    private Integer notaEstadoMoradia;

    @Column(name = "nota_custo_beneficio", nullable = false)
    private Integer notaCustoBeneficio;

    @Column(length = 1000)
    private String comentario;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private Boolean ativa;
}
