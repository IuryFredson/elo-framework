package com.apto.controller;

import com.apto.dto.response.ReputacaoAnuncianteResponseDTO;
import com.apto.service.ReputacaoCalculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/reputacao")
public class ReputacaoController {

    private final ReputacaoCalculoService reputacaoCalculoService;

    public ReputacaoController(ReputacaoCalculoService reputacaoCalculoService) {
        this.reputacaoCalculoService = reputacaoCalculoService;
    }

    @GetMapping("/anunciante/{perfilAnuncianteId}")
    public ResponseEntity<ReputacaoAnuncianteResponseDTO> buscarPorAnunciante(
            @PathVariable UUID perfilAnuncianteId) {
        return ResponseEntity.ok(reputacaoCalculoService.buscarPorAnunciante(perfilAnuncianteId));
    }
}