package com.mentormatch.model.entity;

import com.elo.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "participantes_mentoria")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
public abstract class ParticipanteMentoria extends Usuario {
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "perfil_mentoria_id")
    private PerfilMentoria perfilMentoria;
}
