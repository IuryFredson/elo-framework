package com.apto.controller;

import com.apto.dto.request.ModerarDenunciaRequestDTO;
import com.apto.dto.response.ModeracaoResponseDTO;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Denuncia;
import com.apto.service.ModeracaoService;
import com.elo.web.ModeracaoRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moderacoes/denuncias")
public class ModeracaoController extends ModeracaoRestController<
        Denuncia,
        Anuncio,
        ModerarDenunciaRequestDTO,
        ModeracaoResponseDTO> {

    private final ModeracaoService moderacaoService;

    public ModeracaoController(ModeracaoService moderacaoService) {
        this.moderacaoService = moderacaoService;
    }

    @Override
    protected ModeracaoService service() {
        return moderacaoService;
    }
}