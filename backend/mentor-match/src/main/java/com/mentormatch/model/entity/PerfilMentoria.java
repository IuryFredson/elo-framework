package com.mentormatch.model.entity;

import com.elo.perfil.Perfil;
import com.mentormatch.model.enums.ModalidadeMentoria;
import com.mentormatch.model.enums.NivelConhecimento;
import com.mentormatch.model.enums.PeriodoDisponibilidade;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "perfis_mentoria")
@Getter @Setter @NoArgsConstructor
public class PerfilMentoria implements Perfil {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ElementCollection
    @CollectionTable(name = "perfil_mentoria_areas", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "area")
    private Set<String> areas = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "perfil_mentoria_objetivos", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "objetivo")
    private Set<String> objetivos = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    private NivelConhecimento nivelConhecimento;

    @ElementCollection @Enumerated(EnumType.STRING)
    @CollectionTable(name = "perfil_mentoria_modalidades", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "modalidade")
    private Set<ModalidadeMentoria> modalidades = new LinkedHashSet<>();

    @ElementCollection @Enumerated(EnumType.STRING)
    @CollectionTable(name = "perfil_mentoria_disponibilidades", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "periodo")
    private Set<PeriodoDisponibilidade> disponibilidades = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "perfil_mentoria_idiomas", joinColumns = @JoinColumn(name = "perfil_id"))
    @Column(name = "idioma")
    private Set<String> idiomas = new LinkedHashSet<>();

    @Column(length = 1000)
    private String descricao;

    @Override public String tipoPerfil() { return "PERFIL_MENTORIA"; }
}
