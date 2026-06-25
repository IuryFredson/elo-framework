package com.apto.dto.response;


import com.apto.model.enums.StatusDenuncia;

import java.time.LocalDateTime;
import java.util.UUID;

public record DenunciaResponseDTO(
        UUID id,
        UUID denuncianteId,
        UUID anuncioId,
        String titulo,
        String corpo,
        StatusDenuncia statusDenuncia,
        LocalDateTime criadoEm
) {}
