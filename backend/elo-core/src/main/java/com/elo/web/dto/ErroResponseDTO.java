package com.elo.web.dto;

public record ErroResponseDTO(
        String erro,
        String codigo,
        int status
) {
}
