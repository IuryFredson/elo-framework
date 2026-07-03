package com.studybuddy.controller;

import com.studybuddy.dto.request.ModerarDenunciaGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.ModeracaoGrupoEstudoResponseDTO;
import com.studybuddy.service.ModeracaoGrupoEstudoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/study-buddy/moderacoes/denuncias")
public class ModeracaoGrupoEstudoController {

    private final ModeracaoGrupoEstudoService moderacaoService;

    public ModeracaoGrupoEstudoController(ModeracaoGrupoEstudoService moderacaoService) {
        this.moderacaoService = moderacaoService;
    }

    @PatchMapping("/{denunciaId}")
    public ResponseEntity<ModeracaoGrupoEstudoResponseDTO> moderar(
            @PathVariable UUID denunciaId,
            @Valid @RequestBody ModerarDenunciaGrupoEstudoRequestDTO dto) {
        return ResponseEntity.ok(moderacaoService.moderar(denunciaId, dto));
    }
}
