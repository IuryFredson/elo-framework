package com.apto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReputacaoLocadorResponseDTO {

    private UUID id;
    private UUID locadorId;
    private Double reputacaoScore;
    private Integer totalAvaliacoes;
    private Double mediaGeral;
    private Double mediaComunicacao;
    private Double mediaFidelidadeAnuncio;
    private Double mediaEstadoMoradia;
    private Double mediaCustoBeneficio;
    private LocalDateTime ultimaAtualizacao;
}
