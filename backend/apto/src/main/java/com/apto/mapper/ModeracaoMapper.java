package com.apto.mapper;

import com.apto.dto.request.ModerarDenunciaRequestDTO;
import com.apto.dto.response.ModeracaoResponseDTO;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Denuncia;
import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.StatusDenuncia;
import org.springframework.stereotype.Component;

@Component
public class ModeracaoMapper {

    public ModeracaoResponseDTO toResponseDTO(
            Denuncia denuncia,
            Anuncio anuncio,
            StatusDenuncia statusAnteriorDenuncia,
            StatusAnuncio statusAnteriorAnuncio,
            ModerarDenunciaRequestDTO dto) {
        return new ModeracaoResponseDTO(
                denuncia.getId(),
                anuncio.getId(),
                statusAnteriorDenuncia,
                denuncia.getStatusDenuncia(),
                dto.acaoAnuncio(),
                statusAnteriorAnuncio,
                anuncio.getStatus(),
                dto.justificativa(),
                denuncia.getStatusAtualizadoEm()
        );
    }
}
