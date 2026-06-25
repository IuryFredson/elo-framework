package com.apto.model.entity;

import com.apto.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "perfis_convivencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilConvivencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HorarioSono horarioSono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelBarulho nivelBarulhoAceitavel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FrequenciaVisitas frequenciaVisitas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelOrganizacao nivelOrganizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RotinaEstudos rotinaEstudos;

    @Column(nullable = false)
    private Boolean consomeAlcool;

    @Column(nullable = false)
    private Boolean fumante;

    @Column(nullable = false)
    private Boolean aceitaAnimais;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreferenciaGeneroConvivencia preferenciaGeneroConvivencia;

    @Column(length = 1000)
    private String descricaoLivre;
}