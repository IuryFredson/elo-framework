package com.apto.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AtualizarLocadorRequestDTO(

        @NotBlank String nome,

        @Email
        @NotBlank String email,

        @NotBlank String telefone,

        @NotBlank String documentoIdentificacao,

        @NotBlank String nomeExibicaoOuRazao
) {}