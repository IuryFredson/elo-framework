package com.apto.model.enums;

import com.elo.denuncia.CriterioDenuncia;

public enum CriterioDenunciaApto implements CriterioDenuncia {
    ANUNCIO_ENGANOSO,
    PRECO_ABUSIVO,
    IMOVEL_INEXISTENTE,
    CONTEUDO_INAPROPRIADO,
    OUTRO;

    @Override
    public String codigo() {
        return name();
    }
}
