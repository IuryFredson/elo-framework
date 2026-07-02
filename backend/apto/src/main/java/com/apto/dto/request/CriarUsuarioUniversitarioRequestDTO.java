package com.apto.dto.request;

import com.apto.model.enums.Genero;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CriarUsuarioUniversitarioRequestDTO(

        @NotBlank String nome,

        @Email
        @NotBlank String email,

        @NotBlank String telefone,

        @Email
        @NotBlank String emailInstitucional,

        @NotBlank String curso,

        @NotNull
        @Past
        LocalDate dataNascimento,

        @NotNull
        Genero genero
) {}