package com.studybuddy.controller;

import com.elo.web.ModeracaoRestController;
import com.studybuddy.dto.request.ModerarDenunciaGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.ModeracaoGrupoEstudoResponseDTO;
import com.studybuddy.model.entity.DenunciaGrupoEstudo;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.service.ModeracaoGrupoEstudoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/study-buddy/moderacoes/denuncias")
public class ModeracaoGrupoEstudoController extends ModeracaoRestController<
        DenunciaGrupoEstudo,
        GrupoEstudo,
        ModerarDenunciaGrupoEstudoRequestDTO,
        ModeracaoGrupoEstudoResponseDTO> {

    private final ModeracaoGrupoEstudoService moderacaoService;

    public ModeracaoGrupoEstudoController(ModeracaoGrupoEstudoService moderacaoService) {
        this.moderacaoService = moderacaoService;
    }

    @Override
    protected ModeracaoGrupoEstudoService service() {
        return moderacaoService;
    }
}