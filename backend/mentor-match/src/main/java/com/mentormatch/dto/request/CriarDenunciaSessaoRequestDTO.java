package com.mentormatch.dto.request;

import com.mentormatch.model.enums.CriterioDenunciaMentorMatch;
import java.util.UUID;

public record CriarDenunciaSessaoRequestDTO(
        UUID denuncianteId, UUID sessaoId, String titulo, String corpo,
        CriterioDenunciaMentorMatch criterio) {
    public CriterioDenunciaMentorMatch criterioOuOutro() {
        return criterio == null ? CriterioDenunciaMentorMatch.OUTRO : criterio;
    }
}
