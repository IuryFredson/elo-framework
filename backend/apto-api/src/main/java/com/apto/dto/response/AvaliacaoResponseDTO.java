package com.apto.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvaliacaoResponseDTO(
        UUID id,
        UUID avaliadorId,
        String avaliadorNome,
        UUID anuncianteAvaliadoId,
        String anuncianteAvaliadoNome,
        UUID moradiaId,
        UUID anuncioId,
        String anuncioTitulo,
        Integer notaGeral,
        Integer notaComunicacao,
        Integer notaFidelidadeAnuncio,
        Integer notaEstadoMoradia,
        Integer notaCustoBeneficio,
        String comentario,
        LocalDateTime dataCriacao,
        Boolean ativa
) {}
