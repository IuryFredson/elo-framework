package com.apto.model.entity;

import com.apto.model.enums.StatusManifestacaoInteresse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "manifestacao_interesse")
public class ManifestacaoInteresse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_universitario_id", nullable = false)
    private UsuarioUniversitario interessado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_manifestacao", nullable = false)
    private StatusManifestacaoInteresse status;

    @Column(length = 500)
    private String mensagem;

    @Column(name = "data_manifestacao", nullable = false, updatable = false)
    private LocalDateTime dataManifestacao;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;
}
