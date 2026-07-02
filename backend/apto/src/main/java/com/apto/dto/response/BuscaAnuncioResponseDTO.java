package com.apto.dto.response;

import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BuscaAnuncioResponseDTO(
        UUID id,
        String titulo,
        String descricao,
        BigDecimal valorMensal,
        TipoAnuncio tipoAnuncio,
        StatusAnuncio status,
        LocalDate dataPublicacao,
        UUID moradiaId,
        TipoMoradia tipoMoradia,
        String bairro,
        String enderecoResumo,
        boolean mobiliado,
        boolean aceitaAnimais,
        int quantidadeVagas,
        String nomeAnunciante
) {}
