package com.apto.controller;

import com.apto.dto.request.AlterarStatusUsuarioRequestDTO;
import com.apto.dto.request.AtualizarLocadorRequestDTO;
import com.apto.dto.request.CriarLocadorRequestDTO;
import com.apto.dto.response.LocadorResponseDTO;
import com.apto.service.LocadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios/locadores")
public class LocadorController {

    private final LocadorService service;

    public LocadorController(LocadorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LocadorResponseDTO> criar(
            @Valid @RequestBody CriarLocadorRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<LocadorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocadorResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocadorResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarLocadorRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}/ativo")
    public ResponseEntity<LocadorResponseDTO> alterarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AlterarStatusUsuarioRequestDTO dto
    ) {
        return ResponseEntity.ok(service.alterarStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}