package com.studybuddy.controller;

import com.elo.web.UsuarioRestController;
import com.studybuddy.dto.request.AtualizarEstudanteRequestDTO;
import com.studybuddy.dto.request.CriarEstudanteRequestDTO;
import com.studybuddy.dto.response.EstudanteResponseDTO;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.service.EstudanteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/study-buddy/usuarios")
public class EstudanteController extends UsuarioRestController<
        Estudante,
        CriarEstudanteRequestDTO,
        AtualizarEstudanteRequestDTO,
        EstudanteResponseDTO> {

    private final EstudanteService estudanteService;

    public EstudanteController(EstudanteService estudanteService) {
        this.estudanteService = estudanteService;
    }

    @Override
    protected EstudanteService service() {
        return estudanteService;
    }
}