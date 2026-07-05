package com.apto.controller;

import com.apto.dto.request.AtualizarPerfilRequestDTO;
import com.apto.dto.response.PerfilResponseDTO;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.service.PerfilService;
import com.elo.web.PerfilRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class PerfilController extends PerfilRestController<
        UsuarioUniversitario,
        PerfilConvivencia,
        AtualizarPerfilRequestDTO,
        PerfilResponseDTO> {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @Override
    protected PerfilService service() {
        return perfilService;
    }
}