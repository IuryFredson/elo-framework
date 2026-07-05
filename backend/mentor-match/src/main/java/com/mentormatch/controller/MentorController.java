package com.mentormatch.controller;

import com.elo.web.UsuarioRestController;
import com.mentormatch.dto.request.*;
import com.mentormatch.dto.response.ParticipanteResponseDTO;
import com.mentormatch.model.entity.Mentor;
import com.mentormatch.service.MentorService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/mentor-match/mentores")
public class MentorController extends UsuarioRestController<Mentor, CriarParticipanteRequestDTO, AtualizarParticipanteRequestDTO, ParticipanteResponseDTO> {
    private final MentorService service;
    public MentorController(MentorService service) { this.service = service; }
    @Override protected MentorService service() { return service; }
}
