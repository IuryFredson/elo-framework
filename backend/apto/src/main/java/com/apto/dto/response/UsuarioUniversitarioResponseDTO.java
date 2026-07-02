package com.apto.dto.response;

import com.apto.model.enums.Genero;

import java.time.LocalDate;
import java.util.UUID;

public record UsuarioUniversitarioResponseDTO(
        UUID id,
        String nome,
        String email,
        String telefone,
        Boolean ativo,
        String emailInstitucional,
        String curso,
        LocalDate dataNascimento,
        Genero genero
) {}