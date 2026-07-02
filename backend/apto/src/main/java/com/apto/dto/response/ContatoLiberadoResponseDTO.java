package com.apto.dto.response;

public record ContatoLiberadoResponseDTO(
        String nome,
        String email,
        String telefone,
        String emailInstitucional
) {}
