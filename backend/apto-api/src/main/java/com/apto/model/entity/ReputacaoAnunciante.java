package com.apto.model.entity;

import com.apto.model.PerfilAnunciante;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reputacao_anunciante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReputacaoAnunciante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name="perfil_anunciante_id", nullable = false, unique = true)
    private PerfilAnunciante perfilAnunciante;

    @Column(name="reputacao_score", nullable = false)
    private Double reputacaoScore;

    @Column(name="total_avaliacoes", nullable = false)
    private Integer totalAvaliacoes;

    @Column(name="media_geral", nullable = false)
    private Double mediaGeral;

    @Column(name="media_comunicacao", nullable = false)
    private Double mediaComunicacao;

    @Column(name="media_fidelidade_anuncio", nullable = false)
    private Double mediaFidelidadeAnuncio;

    @Column(name="media_estado_moradia", nullable = false)
    private Double mediaEstadoMoradia;

    @Column(name="media_custo_beneficio", nullable=false)
    private Double mediaCustoBeneficio;

    @Column(name="ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;
}
