package com.mentormatch.controller;

import com.elo.web.ManifestacaoInteresseRestController;
import com.mentormatch.dto.request.CriarSolicitacaoMentoriaRequestDTO;
import com.mentormatch.dto.response.SolicitacaoMentoriaResponseDTO;
import com.mentormatch.model.entity.*;
import com.mentormatch.service.SolicitacaoMentoriaService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/mentor-match/solicitacoes")
public class SolicitacaoMentoriaController extends ManifestacaoInteresseRestController<SolicitacaoMentoria,
        SessaoMentoria, Aluno, CriarSolicitacaoMentoriaRequestDTO, SolicitacaoMentoriaResponseDTO,
        SolicitacaoMentoriaResponseDTO> {
    private final SolicitacaoMentoriaService service;
    public SolicitacaoMentoriaController(SolicitacaoMentoriaService service) { this.service = service; }
    @Override protected SolicitacaoMentoriaService service() { return service; }
}
