package com.studybuddy.controller;

import com.studybuddy.dto.request.AtualizarGrupoEstudoRequestDTO;
import com.studybuddy.dto.request.CriarGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.GrupoEstudoResponseDTO;
import com.studybuddy.model.enums.StatusGrupoEstudo;
import com.studybuddy.service.GrupoEstudoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/study-buddy/grupos")
public class GrupoEstudoController {

    private final GrupoEstudoService grupoEstudoService;

    public GrupoEstudoController(GrupoEstudoService grupoEstudoService) {
        this.grupoEstudoService = grupoEstudoService;
    }

    @PostMapping
    public ResponseEntity<GrupoEstudoResponseDTO> criar(
            @Valid @RequestBody CriarGrupoEstudoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoEstudoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<GrupoEstudoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(grupoEstudoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoEstudoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(grupoEstudoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrupoEstudoResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestParam UUID publicadorId,
            @Valid @RequestBody AtualizarGrupoEstudoRequestDTO dto) {
        return ResponseEntity.ok(grupoEstudoService.atualizar(id, publicadorId, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<GrupoEstudoResponseDTO> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody StatusGrupoEstudo novoStatus) {
        return ResponseEntity.ok(grupoEstudoService.atualizarStatus(id, novoStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        grupoEstudoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
