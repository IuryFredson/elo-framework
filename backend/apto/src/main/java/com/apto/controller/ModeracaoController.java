package com.apto.controller;

import com.apto.dto.request.ModerarDenunciaRequestDTO;
import com.apto.dto.response.ModeracaoResponseDTO;
import com.apto.service.ModeracaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/moderacoes/denuncias")
public class ModeracaoController {

    private final ModeracaoService moderacaoService;

    public ModeracaoController(ModeracaoService moderacaoService) {
        this.moderacaoService = moderacaoService;
    }

    @PatchMapping("/{denunciaId}")
    public ResponseEntity<ModeracaoResponseDTO> moderar(
            @PathVariable UUID denunciaId,
            @Valid @RequestBody ModerarDenunciaRequestDTO dto
    ) {
        return ResponseEntity.ok(moderacaoService.moderar(denunciaId, dto));
    }
}