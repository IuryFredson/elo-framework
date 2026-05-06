package com.apto.dto.response;

import java.util.UUID;

public record ResumoAvaliacoesLocadorResponseDTO(
        UUID locadorId,
        String nomeLocador,
        long totalAvaliacoes,
        Double notaMediaGeral,
        Double notaMediaComunicacao,
        Double notaMediaFidelidadeAnuncio,
        Double notaMediaEstadoMoradia,
        Double notaMediaCustoBeneficio
) {}
