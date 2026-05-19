package com.apto.mapper;

import com.apto.dto.response.PerfilAnuncianteResponseDTO;
import com.apto.model.entity.PerfilAnunciante;
import org.springframework.stereotype.Component;

@Component
public class PerfilAnuncianteMapper {

    public PerfilAnuncianteResponseDTO toResponseDTO(PerfilAnunciante perfil) {
        return new PerfilAnuncianteResponseDTO(
                perfil.getId(),
                perfil.getUsuario().getId(),
                perfil.getUsuario().getNome(),
                perfil.isAtivo());
    }
}
