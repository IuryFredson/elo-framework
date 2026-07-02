package com.studybuddy.mapper;

import com.studybuddy.dto.response.ManifestacaoInteresseGrupoResponseDTO;
import com.studybuddy.model.entity.ManifestacaoInteresseGrupo;
import org.springframework.stereotype.Component;

@Component
public class ManifestacaoInteresseGrupoMapper {

    public ManifestacaoInteresseGrupoResponseDTO toResponseDTO(ManifestacaoInteresseGrupo manifestacao) {
        return new ManifestacaoInteresseGrupoResponseDTO(
                manifestacao.getId(),
                manifestacao.getGrupo().getId(),
                manifestacao.getGrupo().getTitulo(),
                manifestacao.getInteressado().getId(),
                manifestacao.getInteressado().getNome(),
                manifestacao.getGrupo().getPublicadorId(),
                manifestacao.getGrupo().getPublicador().getNome(),
                manifestacao.getStatus(),
                manifestacao.getMensagem(),
                manifestacao.getDataManifestacao(),
                manifestacao.getDataResposta()
        );
    }
}
