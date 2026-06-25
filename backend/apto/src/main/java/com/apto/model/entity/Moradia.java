package com.apto.model.entity;

import com.apto.model.enums.TipoMoradia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "moradia")
public class Moradia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_moradia", nullable = false)
    private TipoMoradia tipoMoradia;

    @Column(nullable = false)
    private String bairro;

    @Column(name = "endereco_resumo", nullable = false)
    private String enderecoResumo;

    @Column(nullable = false)
    private boolean mobiliado;

    @Column(name="aceita_animais",nullable = false)
    private boolean aceitaAnimais;

    @Column(name = "quantidade_vagas", nullable = false)
    private int quantidadeVagas;

    @Column(name = "regras_moradia", nullable = false)
    private String regrasMoradia;
}
