package com.studybuddy.controller;

import com.studybuddy.dto.request.AlterarStatusEstudanteRequestDTO;
import com.studybuddy.dto.request.AtualizarEstudanteRequestDTO;
import com.studybuddy.dto.request.CriarEstudanteRequestDTO;
import com.studybuddy.dto.response.EstudanteResponseDTO;
import com.studybuddy.service.EstudanteService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/study-buddy/estudantes")
public class EstudanteController {

    private final EstudanteService estudanteService;

    public EstudanteController(EstudanteService estudanteService) {
        this.estudanteService = estudanteService;
    }

    @PostMapping
    public ResponseEntity<EstudanteResponseDTO> criar(
            @Valid @RequestBody CriarEstudanteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estudanteService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<EstudanteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(estudanteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudanteResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(estudanteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudanteResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarEstudanteRequestDTO dto) {
        return ResponseEntity.ok(estudanteService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EstudanteResponseDTO> alterarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AlterarStatusEstudanteRequestDTO dto) {
        return ResponseEntity.ok(estudanteService.alterarStatus(id, dto.ativo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        estudanteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
