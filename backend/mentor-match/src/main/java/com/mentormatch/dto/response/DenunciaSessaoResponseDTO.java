package com.mentormatch.dto.response;

import com.elo.denuncia.StatusDenuncia;
import com.mentormatch.model.enums.CriterioDenunciaMentorMatch;
import java.time.LocalDateTime;
import java.util.UUID;

public record DenunciaSessaoResponseDTO(
        UUID id, UUID denuncianteId, UUID sessaoId, String titulo, String corpo,
        CriterioDenunciaMentorMatch criterio, StatusDenuncia status, LocalDateTime criadoEm) { }
