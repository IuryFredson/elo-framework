package com.studybuddy.controller;

import com.studybuddy.dto.request.CriarManifestacaoInteresseGrupoRequestDTO;
import com.studybuddy.dto.response.ManifestacaoInteresseGrupoResponseDTO;
import com.studybuddy.service.ManifestacaoInteresseGrupoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/study-buddy")
public class ManifestacaoInteresseGrupoController {

    private final ManifestacaoInteresseGrupoService manifestacaoService;

    public ManifestacaoInteresseGrupoController(ManifestacaoInteresseGrupoService manifestacaoService) {
        this.manifestacaoService = manifestacaoService;
    }

    @PostMapping("/manifestacoes")
    public ResponseEntity<ManifestacaoInteresseGrupoResponseDTO> criar(
            @Valid @RequestBody CriarManifestacaoInteresseGrupoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manifestacaoService.criar(dto));
    }

    @PostMapping("/manifestacoes/{id}/aceitar")
    public ResponseEntity<ManifestacaoInteresseGrupoResponseDTO> aceitar(
            @PathVariable UUID id,
            @RequestParam UUID publicadorId) {
        return ResponseEntity.ok(manifestacaoService.aceitar(id, publicadorId));
    }

    @PostMapping("/manifestacoes/{id}/recusar")
    public ResponseEntity<ManifestacaoInteresseGrupoResponseDTO> recusar(
            @PathVariable UUID id,
            @RequestParam UUID publicadorId) {
        return ResponseEntity.ok(manifestacaoService.recusar(id, publicadorId));
    }

    @PostMapping("/manifestacoes/{id}/cancelar")
    public ResponseEntity<ManifestacaoInteresseGrupoResponseDTO> cancelar(
            @PathVariable UUID id,
            @RequestParam UUID interessadoId) {
        return ResponseEntity.ok(manifestacaoService.cancelar(id, interessadoId));
    }

    @GetMapping("/grupos/{id}/manifestacoes")
    public ResponseEntity<List<ManifestacaoInteresseGrupoResponseDTO>> listarPorGrupo(
            @PathVariable UUID id,
            @RequestParam UUID publicadorId) {
        return ResponseEntity.ok(manifestacaoService.listarPorGrupo(id, publicadorId));
    }

    @GetMapping("/estudantes/{id}/manifestacoes")
    public ResponseEntity<List<ManifestacaoInteresseGrupoResponseDTO>> listarPorEstudante(
            @PathVariable UUID id) {
        return ResponseEntity.ok(manifestacaoService.listarPorInteressado(id));
    }
}
