package com.apto.controller;

import com.apto.dto.request.CriarManifestacaoInteresseRequestDTO;
import com.apto.dto.response.ManifestacaoInteresseDetalheResponseDTO;
import com.apto.dto.response.ManifestacaoInteresseResponseDTO;
import com.apto.service.ManifestacaoInteresseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manifestacoes-interesse")
public class ManifestacaoInteresseController {

    private final ManifestacaoInteresseService manifestacaoInteresseService;

    public ManifestacaoInteresseController(ManifestacaoInteresseService manifestacaoInteresseService) {
        this.manifestacaoInteresseService = manifestacaoInteresseService;
    }

    @PostMapping
    public ResponseEntity<ManifestacaoInteresseDetalheResponseDTO> criar(
            @Valid @RequestBody CriarManifestacaoInteresseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manifestacaoInteresseService.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManifestacaoInteresseDetalheResponseDTO> buscarPorId(
            @PathVariable UUID id,
            @RequestParam UUID solicitanteId) {
        return ResponseEntity.ok(manifestacaoInteresseService.buscarPorId(id, solicitanteId));
    }

    @GetMapping("/anuncio/{anuncioId}")
    public ResponseEntity<List<ManifestacaoInteresseResponseDTO>> listarPorAnuncio(
            @PathVariable UUID anuncioId,
            @RequestParam UUID anuncianteId) {
        return ResponseEntity.ok(manifestacaoInteresseService.listarPorAnuncio(anuncioId, anuncianteId));
    }

    @GetMapping("/usuario/{interessadoId}")
    public ResponseEntity<List<ManifestacaoInteresseResponseDTO>> listarPorInteressado(
            @PathVariable UUID interessadoId) {
        return ResponseEntity.ok(manifestacaoInteresseService.listarPorInteressado(interessadoId));
    }

    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<ManifestacaoInteresseDetalheResponseDTO> aceitar(
            @PathVariable UUID id,
            @RequestParam UUID anuncianteId) {
        return ResponseEntity.ok(manifestacaoInteresseService.aceitar(id, anuncianteId));
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<ManifestacaoInteresseDetalheResponseDTO> recusar(
            @PathVariable UUID id,
            @RequestParam UUID anuncianteId) {
        return ResponseEntity.ok(manifestacaoInteresseService.recusar(id, anuncianteId));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ManifestacaoInteresseDetalheResponseDTO> cancelar(
            @PathVariable UUID id,
            @RequestParam UUID interessadoId) {
        return ResponseEntity.ok(manifestacaoInteresseService.cancelar(id, interessadoId));
    }
}
