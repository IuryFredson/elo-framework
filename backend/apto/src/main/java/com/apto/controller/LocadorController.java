package com.apto.controller;

import com.apto.dto.request.AtualizarLocadorRequestDTO;
import com.apto.dto.request.CriarLocadorRequestDTO;
import com.apto.dto.response.LocadorResponseDTO;
import com.apto.model.entity.Locador;
import com.apto.service.LocadorService;
import com.elo.web.UsuarioRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios/locadores")
public class LocadorController extends UsuarioRestController<
        Locador,
        CriarLocadorRequestDTO,
        AtualizarLocadorRequestDTO,
        LocadorResponseDTO> {

    private final LocadorService locadorService;

    public LocadorController(LocadorService locadorService) {
        this.locadorService = locadorService;
    }

    @Override
    protected LocadorService service() {
        return locadorService;
    }
}