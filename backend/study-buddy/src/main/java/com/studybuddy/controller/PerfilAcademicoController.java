package com.studybuddy.controller;

import com.studybuddy.dto.request.AtualizarPerfilAcademicoRequestDTO;
import com.studybuddy.dto.response.PerfilAcademicoResponseDTO;
import com.studybuddy.service.PerfilAcademicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/study-buddy/estudantes/{id}/perfil")
public class PerfilAcademicoController {

    private final PerfilAcademicoService perfilAcademicoService;

    public PerfilAcademicoController(PerfilAcademicoService perfilAcademicoService) {
        this.perfilAcademicoService = perfilAcademicoService;
    }

    @GetMapping
    public ResponseEntity<PerfilAcademicoResponseDTO> buscarPerfil(@PathVariable UUID id) {
        return ResponseEntity.ok(perfilAcademicoService.buscarPerfil(id));
    }

    @PutMapping
    public ResponseEntity<PerfilAcademicoResponseDTO> atualizarPerfil(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarPerfilAcademicoRequestDTO dto) {
        return ResponseEntity.ok(perfilAcademicoService.atualizarPerfil(id, dto));
    }
}
