package com.apto.mapper;

import com.apto.dto.response.PerfilResponseDTO;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.elo.porta.MapperResposta;
import org.springframework.stereotype.Component;

@Component
public class PerfilMapper implements MapperResposta<UsuarioUniversitario, PerfilResponseDTO> {

    @Override
    public PerfilResponseDTO paraResposta(UsuarioUniversitario usuario) {
        return toResponseDTO(usuario);
    }

    public PerfilResponseDTO toResponseDTO(UsuarioUniversitario usuario) {
        PerfilConvivencia perfil = usuario.getPerfilConvivencia();

        return new PerfilResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getEmailInstitucional(),
                usuario.getTelefone(),
                usuario.getCurso(),
                usuario.getDataNascimento(),
                usuario.getGenero(),
                perfil != null ? perfil.getHorarioSono() : null,
                perfil != null ? perfil.getNivelBarulhoAceitavel() : null,
                perfil != null ? perfil.getFrequenciaVisitas() : null,
                perfil != null ? perfil.getNivelOrganizacao() : null,
                perfil != null ? perfil.getRotinaEstudos() : null,
                perfil != null ? perfil.getConsomeAlcool() : null,
                perfil != null ? perfil.getFumante() : null,
                perfil != null ? perfil.getAceitaAnimais() : null,
                perfil != null ? perfil.getPreferenciaGeneroConvivencia() : null,
                perfil != null ? perfil.getDescricaoLivre() : null
        );
    }
}
