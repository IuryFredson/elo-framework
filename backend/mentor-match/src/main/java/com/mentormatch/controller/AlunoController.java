package com.mentormatch.controller;

import com.elo.web.UsuarioRestController;
import com.mentormatch.dto.request.*;
import com.mentormatch.dto.response.ParticipanteResponseDTO;
import com.mentormatch.model.entity.Aluno;
import com.mentormatch.service.AlunoService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/mentor-match/alunos")
public class AlunoController extends UsuarioRestController<Aluno, CriarParticipanteRequestDTO, AtualizarParticipanteRequestDTO, ParticipanteResponseDTO> {
    private final AlunoService service;
    public AlunoController(AlunoService service) { this.service = service; }
    @Override protected AlunoService service() { return service; }
}
