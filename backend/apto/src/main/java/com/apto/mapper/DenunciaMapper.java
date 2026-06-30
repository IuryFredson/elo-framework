package com.apto.mapper;

import com.apto.dto.response.DenunciaResponseDTO;
import com.apto.model.entity.Denuncia;
import org.springframework.stereotype.Component;

@Component
public class DenunciaMapper {

    public DenunciaResponseDTO toResponseDTO(Denuncia denuncia) {
        return new DenunciaResponseDTO(
                denuncia.getId(),
                denuncia.getDenunciante().getId(),
                denuncia.getAnuncio().getId(),
                denuncia.getTitulo(),
                denuncia.getCorpo(),
                denuncia.getCriterio() == null ? com.apto.model.enums.CriterioDenunciaApto.OUTRO : denuncia.getCriterio(),
                denuncia.getStatusDenuncia(),
                denuncia.getCriadoEm()
        );
    }
}
