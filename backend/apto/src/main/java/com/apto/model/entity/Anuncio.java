package com.apto.model.entity;

import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.TipoAnuncio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "anuncio")
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descricao;

    @Column(name = "valor_mensal", nullable = false)
    private BigDecimal valorMensal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_anuncio", nullable = false)
    private TipoAnuncio tipoAnuncio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anuncio", nullable = false)
    private StatusAnuncio status;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDate dataPublicacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "perfil_anunciante_id", nullable = false)
    private PerfilAnunciante anunciante;

    @OneToOne
    @JoinColumn(name = "moradia_id", nullable = false, unique = true)
    private Moradia moradia;

    public UUID getAnuncianteUsuarioId() {
        return anunciante.getUsuario().getId();
    }

    public String getAnuncianteNome() {
        Usuario usuario = anunciante.getUsuario();
        if (usuario instanceof Locador locador) {
            return locador.getNomeExibicaoOuRazao();
        }
        return usuario.getNome();
    }
}