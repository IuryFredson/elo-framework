package com.mentormatch.controller;

import com.elo.web.OfertaRestController;
import com.mentormatch.dto.request.*;
import com.mentormatch.dto.response.SessaoMentoriaResponseDTO;
import com.mentormatch.model.entity.SessaoMentoria;
import com.mentormatch.model.enums.*;
import com.mentormatch.service.SessaoMentoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/mentor-match/sessoes")
public class SessaoMentoriaController extends OfertaRestController<SessaoMentoria, CriarSessaoMentoriaRequestDTO,
        AtualizarSessaoMentoriaRequestDTO, SessaoMentoriaResponseDTO, StatusSessaoMentoria> {
    private final SessaoMentoriaService service;
    public SessaoMentoriaController(SessaoMentoriaService service) { this.service = service; }
    @Override protected SessaoMentoriaService service() { return service; }

    @GetMapping("/busca")
    public ResponseEntity<List<SessaoMentoriaResponseDTO>> buscar(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) ModalidadeMentoria modalidade,
            @RequestParam(required = false) NivelConhecimento nivel,
            @RequestParam(required = false) PeriodoDisponibilidade periodo) {
        return ResponseEntity.ok(service.buscar(area, modalidade, nivel, periodo));
    }
}
