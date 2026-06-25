package com.apto.dto.response;

import com.apto.model.enums.TipoMoradia;

import java.util.UUID;

public record MoradiaResponseDTO(
        UUID id,
        TipoMoradia tipoMoradia,
        String bairro,
        String enderecoResumo,
        boolean mobiliado,
        boolean aceitaAnimais,
        int quantidadeVagas,
        String  regrasMoradia
) {}
