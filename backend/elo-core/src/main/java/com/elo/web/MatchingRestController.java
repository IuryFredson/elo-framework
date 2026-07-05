package com.elo.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public abstract class MatchingRestController<R> {

    protected abstract R calcularMatches(UUID solicitanteId, int topN);

    @GetMapping
    public ResponseEntity<R> buscarMatches(
            @RequestParam UUID solicitanteId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int topN) {
        return ResponseEntity.ok(calcularMatches(solicitanteId, topN));
    }
}
