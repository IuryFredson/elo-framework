package com.studybuddy.controller;

import com.elo.web.DenunciaRestController;
import com.studybuddy.dto.request.CriarDenunciaGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.DenunciaGrupoEstudoResponseDTO;
import com.studybuddy.model.entity.DenunciaGrupoEstudo;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.service.DenunciaGrupoEstudoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/study-buddy/denuncias")
public class DenunciaGrupoEstudoController extends DenunciaRestController<
        DenunciaGrupoEstudo,
        GrupoEstudo,
        Estudante,
        CriarDenunciaGrupoEstudoRequestDTO,
        DenunciaGrupoEstudoResponseDTO> {

    private final DenunciaGrupoEstudoService denunciaService;

    public DenunciaGrupoEstudoController(DenunciaGrupoEstudoService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @Override
    protected DenunciaGrupoEstudoService service() {
        return denunciaService;
    }
}