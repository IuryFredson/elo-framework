package com.apto.dto.response;

import java.util.UUID;

public record ResumoAvaliacoesAnuncianteResponseDTO(
        UUID anuncianteId,
        String anuncianteNome,
        long totalAvaliacoes,
        Double notaMediaGeral,
        Double notaMediaComunicacao,
        Double notaMediaFidelidadeAnuncio,
        Double notaMediaEstadoMoradia,
        Double notaMediaCustoBeneficio
) {}
