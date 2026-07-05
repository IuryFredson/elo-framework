package com.mentormatch.controller;

import com.elo.web.ModeracaoRestController;
import com.mentormatch.dto.request.ModerarDenunciaSessaoRequestDTO;
import com.mentormatch.dto.response.ModeracaoSessaoResponseDTO;
import com.mentormatch.model.entity.*;
import com.mentormatch.service.ModeracaoSessaoMentoriaService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/mentor-match/moderacoes/denuncias")
public class ModeracaoSessaoController extends ModeracaoRestController<DenunciaSessaoMentoria, SessaoMentoria,
        ModerarDenunciaSessaoRequestDTO, ModeracaoSessaoResponseDTO> {
    private final ModeracaoSessaoMentoriaService service;
    public ModeracaoSessaoController(ModeracaoSessaoMentoriaService service) { this.service = service; }
    @Override protected ModeracaoSessaoMentoriaService service() { return service; }
}
