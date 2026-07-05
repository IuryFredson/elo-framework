package com.mentormatch.controller;

import com.elo.web.DenunciaRestController;
import com.mentormatch.dto.request.CriarDenunciaSessaoRequestDTO;
import com.mentormatch.dto.response.DenunciaSessaoResponseDTO;
import com.mentormatch.model.entity.*;
import com.mentormatch.service.DenunciaSessaoMentoriaService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/mentor-match/denuncias")
public class DenunciaSessaoController extends DenunciaRestController<DenunciaSessaoMentoria, SessaoMentoria,
        ParticipanteMentoria, CriarDenunciaSessaoRequestDTO, DenunciaSessaoResponseDTO> {
    private final DenunciaSessaoMentoriaService service;
    public DenunciaSessaoController(DenunciaSessaoMentoriaService service) { this.service = service; }
    @Override protected DenunciaSessaoMentoriaService service() { return service; }
}
