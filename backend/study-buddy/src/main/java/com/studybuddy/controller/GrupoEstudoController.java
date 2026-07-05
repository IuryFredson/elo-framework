package com.studybuddy.controller;

import com.elo.web.OfertaRestController;
import com.studybuddy.dto.request.AtualizarGrupoEstudoRequestDTO;
import com.studybuddy.dto.request.CriarGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.GrupoEstudoResponseDTO;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.enums.StatusGrupoEstudo;
import com.studybuddy.service.GrupoEstudoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/study-buddy/ofertas")
public class GrupoEstudoController extends OfertaRestController<
        GrupoEstudo,
        CriarGrupoEstudoRequestDTO,
        AtualizarGrupoEstudoRequestDTO,
        GrupoEstudoResponseDTO,
        StatusGrupoEstudo> {

    private final GrupoEstudoService grupoEstudoService;

    public GrupoEstudoController(GrupoEstudoService grupoEstudoService) {
        this.grupoEstudoService = grupoEstudoService;
    }

    @Override
    protected GrupoEstudoService service() {
        return grupoEstudoService;
    }
}