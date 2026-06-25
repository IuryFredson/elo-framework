package com.apto.controller;

import com.apto.dto.response.MatchmakingResponseDTO;
import com.apto.service.matchmaking.MatchmakingService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/matchmaking")
@RequiredArgsConstructor
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    @GetMapping("/colegas/{solicitanteId}")
    public ResponseEntity<MatchmakingResponseDTO> buscarColegas(
            @PathVariable UUID solicitanteId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int topN) {

        return ResponseEntity.ok(matchmakingService.buscarColegasCompativeis(solicitanteId, topN));
    }
}