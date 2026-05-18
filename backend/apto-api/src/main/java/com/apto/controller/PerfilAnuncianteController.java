package com.apto.controller;

import com.apto.dto.response.PerfilAnuncianteResponseDTO;
import com.apto.service.PerfilAnuncianteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/perfis-anunciante")
public class PerfilAnuncianteController {

    private final PerfilAnuncianteService perfilAnuncianteService;

    public PerfilAnuncianteController(PerfilAnuncianteService perfilAnuncianteService) {
        this.perfilAnuncianteService = perfilAnuncianteService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<PerfilAnuncianteResponseDTO> buscarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(perfilAnuncianteService.buscarPorUsuario(usuarioId));
    }

    @PostMapping("/universitarios/{universitarioId}")
    public ResponseEntity<PerfilAnuncianteResponseDTO> habilitarAnunciante(
            @PathVariable UUID universitarioId) {
        return ResponseEntity.ok(
                perfilAnuncianteService.habilitarAnunciante(universitarioId));
    }

    @DeleteMapping("/universitarios/{universitarioId}")
    public ResponseEntity<PerfilAnuncianteResponseDTO> desabilitarAnunciante(
            @PathVariable UUID universitarioId) {
        return ResponseEntity.ok(
                perfilAnuncianteService.desabilitarAnunciante(universitarioId));
    }
}