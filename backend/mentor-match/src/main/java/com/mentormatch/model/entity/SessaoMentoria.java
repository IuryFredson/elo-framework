package com.mentormatch.model.entity;

import com.elo.oferta.Oferta;
import com.mentormatch.model.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "sessoes_mentoria")
@Getter @Setter @NoArgsConstructor
public class SessaoMentoria implements Oferta {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    private String titulo;
    @Column(length = 1000) private String descricao;
    private String area;
    @Enumerated(EnumType.STRING) private NivelConhecimento nivelAtendido;
    @Enumerated(EnumType.STRING) private ModalidadeMentoria modalidade;
    @Enumerated(EnumType.STRING) private PeriodoDisponibilidade periodo;
    private int capacidade;
    @Enumerated(EnumType.STRING) private StatusSessaoMentoria status;
    private LocalDate dataPublicacao;
    @ManyToOne(optional = false) @JoinColumn(name = "mentor_id") private Mentor publicador;

    @Override public UUID getPublicadorId() { return publicador == null ? null : publicador.getId(); }
    @Override public String tipoOferta() { return "SESSAO_MENTORIA"; }
    @Override public boolean isAtiva() { return status == StatusSessaoMentoria.ATIVA; }
}
