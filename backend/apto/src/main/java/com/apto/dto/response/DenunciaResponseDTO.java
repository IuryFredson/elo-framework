package com.apto.dto.response;


import com.apto.model.enums.CriterioDenunciaApto;
import com.elo.denuncia.StatusDenuncia;

import java.time.LocalDateTime;
import java.util.UUID;

public record DenunciaResponseDTO(
        UUID id,
        UUID denuncianteId,
        UUID anuncioId,
        String titulo,
        String corpo,
        CriterioDenunciaApto criterio,
        StatusDenuncia statusDenuncia,
        LocalDateTime criadoEm
) {}
