package com.apto.mapper;

import com.apto.dto.response.UsuarioUniversitarioResponseDTO;
import com.apto.model.entity.UsuarioUniversitario;
import com.elo.porta.MapperResposta;
import org.springframework.stereotype.Component;

@Component
public class UsuarioUniversitarioMapper implements MapperResposta<UsuarioUniversitario, UsuarioUniversitarioResponseDTO> {

    @Override
    public UsuarioUniversitarioResponseDTO paraResposta(UsuarioUniversitario usuario) {
        return toResponseDTO(usuario);
    }

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
