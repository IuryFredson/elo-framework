package com.studybuddy.controller;

import com.elo.denuncia.StatusDenuncia;
import com.studybuddy.dto.request.AtualizarStatusDenunciaGrupoEstudoRequestDTO;
import com.studybuddy.dto.request.CriarDenunciaGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.DenunciaGrupoEstudoResponseDTO;
import com.studybuddy.service.DenunciaGrupoEstudoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/study-buddy/denuncias")
public class DenunciaGrupoEstudoController {

    private final DenunciaGrupoEstudoService denunciaService;

    public DenunciaGrupoEstudoController(DenunciaGrupoEstudoService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @GetMapping
    public ResponseEntity<List<DenunciaGrupoEstudoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(denunciaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DenunciaGrupoEstudoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(denunciaService.buscarPorId(id));
    }

    @GetMapping("/por-grupo")
    public ResponseEntity<List<DenunciaGrupoEstudoResponseDTO>> buscarPorGrupo(@RequestParam UUID grupoId) {
        return ResponseEntity.ok(denunciaService.buscarPorGrupoId(grupoId));
    }

    @GetMapping("/por-estudante")
    public ResponseEntity<List<DenunciaGrupoEstudoResponseDTO>> buscarPorEstudante(@RequestParam UUID estudanteId) {
        return ResponseEntity.ok(denunciaService.buscarPorUsuarioId(estudanteId));
    }

    @GetMapping("/por-status")
    public ResponseEntity<List<DenunciaGrupoEstudoResponseDTO>> buscarPorStatus(@RequestParam StatusDenuncia status) {
        return ResponseEntity.ok(denunciaService.buscarPorStatus(status));
    }

    @PostMapping
    public ResponseEntity<DenunciaGrupoEstudoResponseDTO> criar(
            @Valid @RequestBody CriarDenunciaGrupoEstudoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(denunciaService.criar(dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DenunciaGrupoEstudoResponseDTO> atualizarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarStatusDenunciaGrupoEstudoRequestDTO dto) {
        return ResponseEntity.ok(denunciaService.atualizarStatus(id, dto.novoStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        denunciaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
