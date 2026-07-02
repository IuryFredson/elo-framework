package com.studybuddy.model.entity;

import com.elo.usuario.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estudantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estudante extends Usuario {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String matricula;

    @NotBlank
    @Column(nullable = false)
    private String instituicao;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "perfil_academico_id")
    private PerfilAcademico perfilAcademico;
}
