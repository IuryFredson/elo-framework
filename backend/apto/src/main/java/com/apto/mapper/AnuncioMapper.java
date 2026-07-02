package com.apto.mapper;

import com.apto.dto.response.AnuncioResponseDTO;
import com.apto.dto.response.BuscaAnuncioResponseDTO;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Moradia;
import com.elo.porta.MapperResposta;
import org.springframework.stereotype.Component;

@Component
public class AnuncioMapper implements MapperResposta<Anuncio, AnuncioResponseDTO> {

    @Override
    public AnuncioResponseDTO paraResposta(Anuncio anuncio) {
        return toResponseDTO(anuncio);
    }

    public BuscaAnuncioResponseDTO toBuscaResponseDTO(Anuncio anuncio) {
        Moradia moradia = anuncio.getMoradia();
        return new BuscaAnuncioResponseDTO(
                anuncio.getId(),
                anuncio.getTitulo(),
                anuncio.getDescricao(),
                anuncio.getValorMensal(),
                anuncio.getTipoAnuncio(),
                anuncio.getStatus(),
                anuncio.getDataPublicacao(),
                moradia.getId(),
                moradia.getTipoMoradia(),
                moradia.getBairro(),
                moradia.getEnderecoResumo(),
                moradia.isMobiliado(),
                moradia.isAceitaAnimais(),
                moradia.getQuantidadeVagas(),
                anuncio.getAnuncianteNome()
        );
    }

    public AnuncioResponseDTO toResponseDTO(Anuncio anuncio) {
        return new AnuncioResponseDTO(
                anuncio.getId(),
                anuncio.getTitulo(),
                anuncio.getDescricao(),
                anuncio.getValorMensal(),
                anuncio.getTipoAnuncio(),
                anuncio.getStatus(),
                anuncio.getDataPublicacao(),
                anuncio.getAnuncianteUsuarioId(),
                anuncio.getMoradia().getId()
        );
    }
}
