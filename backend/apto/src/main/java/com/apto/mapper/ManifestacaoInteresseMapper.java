package com.apto.mapper;

import com.apto.dto.response.ContatoLiberadoResponseDTO;
import com.apto.dto.response.ManifestacaoInteresseDetalheResponseDTO;
import com.apto.dto.response.ManifestacaoInteresseResponseDTO;
import com.apto.model.entity.ManifestacaoInteresse;
import com.elo.usuario.Usuario;
import com.apto.model.entity.UsuarioUniversitario;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import org.springframework.stereotype.Component;

@Component
public class ManifestacaoInteresseMapper {

    public ManifestacaoInteresseResponseDTO toResponseDTO(ManifestacaoInteresse manifestacao) {
        return new ManifestacaoInteresseResponseDTO(
                manifestacao.getId(),
                manifestacao.getAnuncio().getId(),
                manifestacao.getAnuncio().getTitulo(),
                manifestacao.getInteressado().getId(),
                manifestacao.getInteressado().getNome(),
                manifestacao.getStatus(),
                manifestacao.getMensagem(),
                manifestacao.getDataManifestacao(),
                manifestacao.getDataResposta()
        );
    }

    public ManifestacaoInteresseDetalheResponseDTO toDetalheDTO(ManifestacaoInteresse manifestacao) {
        ContatoLiberadoResponseDTO contatoInteressado = null;
        ContatoLiberadoResponseDTO contatoAnunciante = null;

        if (manifestacao.getStatus() == StatusManifestacaoInteresse.ACEITA) {
            contatoInteressado = montarContatoInteressado(manifestacao.getInteressado());
            contatoAnunciante = montarContatoAnunciante(manifestacao.getAnuncio().getAnunciante().getUsuario());
        }

        return new ManifestacaoInteresseDetalheResponseDTO(
                manifestacao.getId(),
                manifestacao.getAnuncio().getId(),
                manifestacao.getAnuncio().getTitulo(),
                manifestacao.getInteressado().getId(),
                manifestacao.getInteressado().getNome(),
                manifestacao.getStatus(),
                manifestacao.getMensagem(),
                manifestacao.getDataManifestacao(),
                manifestacao.getDataResposta(),
                contatoInteressado,
                contatoAnunciante
        );
    }

    private ContatoLiberadoResponseDTO montarContatoInteressado(UsuarioUniversitario interessado) {
        return new ContatoLiberadoResponseDTO(
                interessado.getNome(),
                interessado.getEmail(),
                interessado.getTelefone(),
                interessado.getEmailInstitucional()
        );
    }

    private ContatoLiberadoResponseDTO montarContatoAnunciante(Usuario anunciante) {
        String emailInstitucional = anunciante instanceof UsuarioUniversitario uu
                ? uu.getEmailInstitucional()
                : null;
        return new ContatoLiberadoResponseDTO(
                anunciante.getNome(),
                anunciante.getEmail(),
                anunciante.getTelefone(),
                emailInstitucional
        );
    }
}

