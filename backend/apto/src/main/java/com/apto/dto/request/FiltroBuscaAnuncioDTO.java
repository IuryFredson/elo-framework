package com.apto.dto.request;

import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;

import java.math.BigDecimal;

public record FiltroBuscaAnuncioDTO(
        BigDecimal valorMin,
        BigDecimal valorMax,
        String bairro,
        TipoMoradia tipoMoradia,
        TipoAnuncio tipoAnuncio,
        Boolean mobiliado,
        Boolean aceitaAnimais,
        Integer quantidadeVagas
) {}
