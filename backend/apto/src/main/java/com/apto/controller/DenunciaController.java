package com.apto.controller;

import com.apto.dto.request.CriarDenunciaRequestDTO;
import com.apto.dto.response.DenunciaResponseDTO;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Denuncia;
import com.apto.service.DenunciaService;
import com.elo.usuario.Usuario;
import com.elo.web.DenunciaRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/denuncias")
public class DenunciaController extends DenunciaRestController<
        Denuncia,
        Anuncio,
        Usuario,
        CriarDenunciaRequestDTO,
        DenunciaResponseDTO> {

    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @Override
    protected DenunciaService service() {
        return denunciaService;
    }
}