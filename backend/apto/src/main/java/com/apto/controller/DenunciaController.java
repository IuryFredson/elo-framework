package com.apto.controller;

import com.apto.dto.request.AtualizarStatusRequestDTO;
import com.apto.dto.request.CriarDenunciaRequestDTO;
import com.apto.dto.response.DenunciaResponseDTO;
import com.apto.model.enums.StatusDenuncia;
import com.apto.service.DenunciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/denuncias")
public class DenunciaController {
    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {this.denunciaService = denunciaService;}

    @GetMapping
    public ResponseEntity<List<DenunciaResponseDTO>> listarTodas(){
        return ResponseEntity.ok(denunciaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DenunciaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(denunciaService.buscarPorId(id));
    }

    @GetMapping("/por-anuncio")
    public ResponseEntity<List<DenunciaResponseDTO>> buscarPorAnuncio(@RequestParam UUID anuncioId) {
        return ResponseEntity.ok(denunciaService.buscarPorAnuncioId(anuncioId));
    }

    @GetMapping("/por-usuario")
    public ResponseEntity<List<DenunciaResponseDTO>> buscarPorUsuario(@RequestParam UUID usuarioId) {
        return ResponseEntity.ok(denunciaService.buscarPorUsuarioId(usuarioId));
    }

    @GetMapping("/por-status")
    public ResponseEntity<List<DenunciaResponseDTO>> buscarPorStatus(@RequestParam StatusDenuncia status) {
        return ResponseEntity.ok(denunciaService.buscarPorStatus(status));
    }

    @PostMapping
    public ResponseEntity<DenunciaResponseDTO> criar(@Valid @RequestBody CriarDenunciaRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(denunciaService.criar(dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DenunciaResponseDTO> atualizarStatus(@PathVariable UUID id, @Valid @RequestBody AtualizarStatusRequestDTO dto){
        return ResponseEntity.ok(denunciaService.atualizarStatus(id, dto.novoStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        denunciaService.deletar(id);
        return ResponseEntity.noContent().build();
    }


}
