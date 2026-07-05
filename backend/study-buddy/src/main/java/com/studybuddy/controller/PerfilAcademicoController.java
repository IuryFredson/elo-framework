package com.studybuddy.controller;

import com.elo.web.PerfilRestController;
import com.studybuddy.dto.request.AtualizarPerfilAcademicoRequestDTO;
import com.studybuddy.dto.response.PerfilAcademicoResponseDTO;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import com.studybuddy.service.PerfilAcademicoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/study-buddy/usuarios")
public class PerfilAcademicoController extends PerfilRestController<
        Estudante,
        PerfilAcademico,
        AtualizarPerfilAcademicoRequestDTO,
        PerfilAcademicoResponseDTO> {

    private final PerfilAcademicoService perfilAcademicoService;

    public PerfilAcademicoController(PerfilAcademicoService perfilAcademicoService) {
        this.perfilAcademicoService = perfilAcademicoService;
    }

    @Override
    protected PerfilAcademicoService service() {
        return perfilAcademicoService;
    }
}