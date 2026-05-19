package com.apto.mapper;

import com.apto.dto.response.ReputacaoAnuncianteResponseDTO;
import com.apto.model.entity.ReputacaoAnunciante;
import org.springframework.stereotype.Component;

@Component
public class ReputacaoAnuncianteMapper {

    public ReputacaoAnuncianteResponseDTO toResponseDTO(ReputacaoAnunciante reputacao) {
        return new ReputacaoAnuncianteResponseDTO(
                reputacao.getId(),
                reputacao.getPerfilAnunciante().getId(),
                reputacao.getPerfilAnunciante().getUsuario().getId(),
                reputacao.getReputacaoScore(),
                reputacao.getTotalAvaliacoes(),
                reputacao.getMediaGeral(),
                reputacao.getMediaComunicacao(),
                reputacao.getMediaFidelidadeAnuncio(),
                reputacao.getMediaEstadoMoradia(),
                reputacao.getMediaCustoBeneficio(),
                reputacao.getUltimaAtualizacao());
    }
}
