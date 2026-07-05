package com.apto.controller;

import com.apto.dto.request.AtualizarUsuarioUniversitarioRequestDTO;
import com.apto.dto.request.CriarUsuarioUniversitarioRequestDTO;
import com.apto.dto.response.UsuarioUniversitarioResponseDTO;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.service.UsuarioUniversitarioService;
import com.elo.web.UsuarioRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios/universitarios")
public class UsuarioUniversitarioController extends UsuarioRestController<
        UsuarioUniversitario,
        CriarUsuarioUniversitarioRequestDTO,
        AtualizarUsuarioUniversitarioRequestDTO,
        UsuarioUniversitarioResponseDTO> {

    private final UsuarioUniversitarioService usuarioService;

    public UsuarioUniversitarioController(UsuarioUniversitarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    protected UsuarioUniversitarioService service() {
        return usuarioService;
    }
}