package com.studybuddy.controller;

import com.elo.web.ManifestacaoInteresseRestController;
import com.studybuddy.dto.request.CriarManifestacaoInteresseGrupoRequestDTO;
import com.studybuddy.dto.response.ManifestacaoInteresseGrupoResponseDTO;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.entity.ManifestacaoInteresseGrupo;
import com.studybuddy.service.ManifestacaoInteresseGrupoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/study-buddy/manifestacoes")
public class ManifestacaoInteresseGrupoController extends ManifestacaoInteresseRestController<
        ManifestacaoInteresseGrupo,
        GrupoEstudo,
        Estudante,
        CriarManifestacaoInteresseGrupoRequestDTO,
        ManifestacaoInteresseGrupoResponseDTO,
        ManifestacaoInteresseGrupoResponseDTO> {

    private final ManifestacaoInteresseGrupoService manifestacaoService;

    public ManifestacaoInteresseGrupoController(ManifestacaoInteresseGrupoService manifestacaoService) {
        this.manifestacaoService = manifestacaoService;
    }

    @Override
    protected ManifestacaoInteresseGrupoService service() {
        return manifestacaoService;
    }
}