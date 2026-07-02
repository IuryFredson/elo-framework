package com.apto.controller;

import com.apto.dto.request.AlterarStatusUsuarioRequestDTO;
import com.apto.dto.request.AtualizarUsuarioUniversitarioRequestDTO;
import com.apto.dto.request.CriarUsuarioUniversitarioRequestDTO;
import com.apto.dto.response.UsuarioUniversitarioResponseDTO;
import com.apto.service.UsuarioUniversitarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios/universitarios")
public class UsuarioUniversitarioController {

    private final UsuarioUniversitarioService service;

    public UsuarioUniversitarioController(UsuarioUniversitarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioUniversitarioResponseDTO> criar(
            @Valid @RequestBody CriarUsuarioUniversitarioRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioUniversitarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioUniversitarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioUniversitarioResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarUsuarioUniversitarioRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}/ativo")
    public ResponseEntity<UsuarioUniversitarioResponseDTO> alterarStatus(
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