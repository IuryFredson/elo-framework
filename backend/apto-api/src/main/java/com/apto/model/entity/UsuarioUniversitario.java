package com.apto.model.entity;

import com.apto.model.PerfilAnunciante;
import com.apto.model.enums.Genero;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios_universitarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUniversitario extends Usuario {

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String emailInstitucional;

    @NotBlank
    @Column(nullable = false)
    private String curso;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "perfil_convivencia_id")
    private PerfilConvivencia perfilConvivencia;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private PerfilAnunciante perfilAnunciante;

    public boolean isAnunciante() {
        return perfilAnunciante != null && perfilAnunciante.isAtivo();
    }
}