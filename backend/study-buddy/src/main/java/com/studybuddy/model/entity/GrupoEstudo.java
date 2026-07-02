package com.studybuddy.model.entity;

import com.elo.oferta.Oferta;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import com.studybuddy.model.enums.StatusGrupoEstudo;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "grupos_estudo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoEstudo implements Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String descricao;

    @NotBlank
    @Column(nullable = false)
    private String disciplina;

    @Positive
    @Column(nullable = false)
    private int quantidadeVagas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModalidadeEstudo modalidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodoDisponibilidade periodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusGrupoEstudo status;

    @Column(nullable = false)
    private LocalDate dataPublicacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "publicador_id", nullable = false)
    private Estudante publicador;

    @Override
    public UUID getPublicadorId() {
        return publicador.getId();
    }

    @Override
    public String tipoOferta() {
        return "GRUPO_ESTUDO";
    }

    @Override
    public boolean isAtiva() {
        return status == StatusGrupoEstudo.ATIVO;
    }
}
