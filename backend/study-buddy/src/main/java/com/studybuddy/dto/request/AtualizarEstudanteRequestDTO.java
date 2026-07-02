package com.studybuddy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AtualizarEstudanteRequestDTO(
        @NotBlank String nome,
        @Email @NotBlank String email,
        String telefone,
        @NotBlank String matricula,
        @NotBlank String instituicao
) {
}
