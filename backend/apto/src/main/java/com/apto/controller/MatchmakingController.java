package com.apto.controller;

import com.apto.dto.response.MatchmakingResponseDTO;
import com.apto.service.matchmaking.MatchmakingService;
import com.elo.web.MatchingRestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/matching")
public class MatchmakingController extends MatchingRestController<MatchmakingResponseDTO> {

    private final MatchmakingService matchmakingService;

    public MatchmakingController(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @Override
    protected MatchmakingResponseDTO calcularMatches(UUID solicitanteId, int topN) {
        return matchmakingService.buscarColegasCompativeis(solicitanteId, topN);
    }
}