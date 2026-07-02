package com.apto.dto.response;

import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.TipoAnuncio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AnuncioResponseDTO(
        UUID id,
        String titulo,
        String descricao,
        BigDecimal valorMensal,
        TipoAnuncio tipoAnuncio,
        StatusAnuncio status,
        LocalDate dataPublicacao,
        UUID anuncianteId,
        UUID moradiaId
) {}