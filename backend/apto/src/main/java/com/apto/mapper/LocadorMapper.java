package com.apto.mapper;

import com.apto.dto.response.LocadorResponseDTO;
import com.apto.model.entity.Locador;
import com.elo.porta.MapperResposta;
import org.springframework.stereotype.Component;

@Component
public class LocadorMapper implements MapperResposta<Locador, LocadorResponseDTO> {

    @Override
    public LocadorResponseDTO paraResposta(Locador locador) {
        return toResponseDTO(locador);
    }

    public LocadorResponseDTO toResponseDTO(Locador locador) {
        return new LocadorResponseDTO(
                locador.getId(),
                locador.getNome(),
                locador.getEmail(),
                locador.getTelefone(),
                locador.isAtivo(),
                locador.getDocumentoIdentificacao(),
                locador.getNomeExibicaoOuRazao());
    }
}
