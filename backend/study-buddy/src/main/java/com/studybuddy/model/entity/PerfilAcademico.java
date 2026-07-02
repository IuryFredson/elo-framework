package com.studybuddy.model.entity;

import com.elo.perfil.Perfil;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.NivelConhecimento;
import com.studybuddy.model.enums.ObjetivoEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "perfis_academicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilAcademico implements Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String curso;

    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "perfil_academico_disciplinas", joinColumns = @JoinColumn(name = "perfil_academico_id"))
    @Column(name = "disciplina", nullable = false)
    private Set<String> disciplinasInteresse = new LinkedHashSet<>();

    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "perfil_academico_disponibilidades", joinColumns = @JoinColumn(name = "perfil_academico_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "periodo", nullable = false)
    private Set<PeriodoDisponibilidade> disponibilidade = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjetivoEstudo objetivoEstudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelConhecimento nivelConhecimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModalidadeEstudo modalidadePreferida;

    @Column(length = 1000)
    private String descricao;

    @Override
    public String tipoPerfil() {
        return "PERFIL_ACADEMICO";
    }
}
