package com.apto.dto.response;

import java.util.UUID;

public record LocadorResponseDTO(
        UUID id,
        String nome,
        String email,
        String telefone,
        Boolean ativo,
        String documentoIdentificacao,
        String nomeExibicaoOuRazao
) {}