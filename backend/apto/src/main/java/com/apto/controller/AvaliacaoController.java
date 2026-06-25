package com.apto.controller;

import com.apto.dto.request.AtualizarAvaliacaoRequestDTO;
import com.apto.dto.request.CriarAvaliacaoRequestDTO;
import com.apto.dto.response.AvaliacaoResponseDTO;
import com.apto.dto.response.ResumoAvaliacoesAnuncianteResponseDTO;
import com.apto.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> criar(@Valid @RequestBody CriarAvaliacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoService.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(avaliacaoService.buscarPorId(id));
    }

    @GetMapping("/anunciante/{perfilAnuncianteId}")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorAnunciante(
            @PathVariable UUID perfilAnuncianteId) {
        return ResponseEntity.ok(avaliacaoService.listarPorAnunciante(perfilAnuncianteId));
    }

    @GetMapping("/anunciante/{perfilAnuncianteId}/resumo")
    public ResponseEntity<ResumoAvaliacoesAnuncianteResponseDTO> resumoPorAnunciante(
            @PathVariable UUID perfilAnuncianteId) {
        return ResponseEntity.ok(avaliacaoService.resumoPorAnunciante(perfilAnuncianteId));
    }

    @GetMapping("/moradia/{moradiaId}")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorMoradia(@PathVariable UUID moradiaId) {
        return ResponseEntity.ok(avaliacaoService.listarPorMoradia(moradiaId));
    }

    @GetMapping("/avaliador/{avaliadorId}")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorAvaliador(@PathVariable UUID avaliadorId) {
        return ResponseEntity.ok(avaliacaoService.listarPorAvaliador(avaliadorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestParam UUID avaliadorId,
            @Valid @RequestBody AtualizarAvaliacaoRequestDTO dto) {
        return ResponseEntity.ok(avaliacaoService.atualizar(id, avaliadorId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable UUID id, @RequestParam UUID avaliadorId) {
        avaliacaoService.desativar(id, avaliadorId);
        return ResponseEntity.noContent().build();
    }
}