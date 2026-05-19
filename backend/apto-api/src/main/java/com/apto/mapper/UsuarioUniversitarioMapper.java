package com.apto.mapper;

import com.apto.dto.response.UsuarioUniversitarioResponseDTO;
import com.apto.model.entity.UsuarioUniversitario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioUniversitarioMapper {

    public UsuarioUniversitarioResponseDTO toResponseDTO(UsuarioUniversitario usuario) {
        return new UsuarioUniversitarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.isAtivo(),
                usuario.getEmailInstitucional(),
                usuario.getCurso(),
                usuario.getDataNascimento(),
                usuario.getGenero()
        );
    }
}
