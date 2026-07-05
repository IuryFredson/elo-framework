package com.apto.controller;

import com.apto.dto.request.AtualizarAnuncioRequestDTO;
import com.apto.dto.request.CriarAnuncioRequestDTO;
import com.apto.dto.request.FiltroBuscaAnuncioDTO;
import com.apto.dto.response.AnuncioResponseDTO;
import com.apto.dto.response.BuscaAnuncioResponseDTO;
import com.apto.dto.response.PaginaResponseDTO;
import com.apto.model.entity.Anuncio;
import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;
import com.apto.service.AnuncioService;
import com.elo.web.OfertaRestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/ofertas")
public class AnuncioController extends OfertaRestController<
        Anuncio,
        CriarAnuncioRequestDTO,
        AtualizarAnuncioRequestDTO,
        AnuncioResponseDTO,
        StatusAnuncio> {

    private final AnuncioService anuncioService;

    public AnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @Override
    protected AnuncioService service() {
        return anuncioService;
    }

    @GetMapping("/busca")
    public ResponseEntity<PaginaResponseDTO<BuscaAnuncioResponseDTO>> buscarAnuncios(
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax,
            @RequestParam(required = false) String bairro,
            @RequestParam(required = false) TipoMoradia tipoMoradia,
            @RequestParam(required = false) TipoAnuncio tipoAnuncio,
            @RequestParam(required = false) Boolean mobiliado,
            @RequestParam(required = false) Boolean aceitaAnimais,
            @RequestParam(required = false) Integer quantidadeVagas,
            @PageableDefault(size = 10, sort = "dataPublicacao") Pageable pageable) {
        FiltroBuscaAnuncioDTO filtro = new FiltroBuscaAnuncioDTO(
                valorMin,
                valorMax,
                bairro,
                tipoMoradia,
                tipoAnuncio,
                mobiliado,
                aceitaAnimais,
                quantidadeVagas);
        return ResponseEntity.ok(anuncioService.buscarAnuncios(filtro, pageable));
    }
}