package com.studybuddy.dto.response;

import java.util.UUID;

public record EstudanteResponseDTO(
        UUID id,
        String nome,
        String email,
        String telefone,
        boolean ativo,
        String matricula,
        String instituicao
) {
}
