package com.apto.dto.response;

import java.util.UUID;

public record PerfilAnuncianteResponseDTO(
        UUID id,
        UUID usuarioId,
        String nomeUsuario,
        boolean ativo
) {}