package com.apto.mapper;

import com.apto.dto.response.MoradiaResponseDTO;
import com.apto.model.entity.Moradia;
import org.springframework.stereotype.Component;

@Component
public class MoradiaMapper {

    public MoradiaResponseDTO toResponseDTO(Moradia moradia) {
        return new MoradiaResponseDTO(
                moradia.getId(),
                moradia.getTipoMoradia(),
                moradia.getBairro(),
                moradia.getEnderecoResumo(),
                moradia.isMobiliado(),
                moradia.isAceitaAnimais(),
                moradia.getQuantidadeVagas(),
                moradia.getRegrasMoradia()
        );
    }
}
