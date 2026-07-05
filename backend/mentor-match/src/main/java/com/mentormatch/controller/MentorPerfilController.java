package com.mentormatch.controller;

import com.elo.web.PerfilRestController;
import com.mentormatch.dto.request.AtualizarPerfilMentoriaRequestDTO;
import com.mentormatch.dto.response.PerfilMentoriaResponseDTO;
import com.mentormatch.model.entity.*;
import com.mentormatch.service.PerfilMentoriaService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/mentor-match/mentores")
public class MentorPerfilController extends PerfilRestController<ParticipanteMentoria, PerfilMentoria, AtualizarPerfilMentoriaRequestDTO, PerfilMentoriaResponseDTO> {
    private final PerfilMentoriaService service;
    public MentorPerfilController(PerfilMentoriaService service) { this.service = service; }
    @Override protected PerfilMentoriaService service() { return service; }
}
