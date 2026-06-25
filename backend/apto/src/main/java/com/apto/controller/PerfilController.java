package com.apto.controller;

import com.apto.dto.request.AtualizarPerfilRequestDTO;
import com.apto.dto.response.PerfilResponseDTO;
import com.apto.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/{id}/perfil")
    public ResponseEntity<PerfilResponseDTO> buscarPerfil(@PathVariable UUID id) {
        return ResponseEntity.ok(perfilService.buscarPerfil(id));
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<PerfilResponseDTO> atualizarPerfil(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarPerfilRequestDTO dto
    ) {
        return ResponseEntity.ok(perfilService.atualizarPerfil(id, dto));
    }
}