package com.apto.controller;

import com.apto.dto.request.CriarManifestacaoInteresseRequestDTO;
import com.apto.dto.response.ManifestacaoInteresseDetalheResponseDTO;
import com.apto.dto.response.ManifestacaoInteresseResponseDTO;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.service.ManifestacaoInteresseService;
import com.elo.web.ManifestacaoInteresseRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manifestacoes")
public class ManifestacaoInteresseController extends ManifestacaoInteresseRestController<
        ManifestacaoInteresse,
        Anuncio,
        UsuarioUniversitario,
        CriarManifestacaoInteresseRequestDTO,
        ManifestacaoInteresseResponseDTO,
        ManifestacaoInteresseDetalheResponseDTO> {

    private final ManifestacaoInteresseService manifestacaoInteresseService;

    public ManifestacaoInteresseController(ManifestacaoInteresseService manifestacaoInteresseService) {
        this.manifestacaoInteresseService = manifestacaoInteresseService;
    }

    @Override
    protected ManifestacaoInteresseService service() {
        return manifestacaoInteresseService;
    }
}