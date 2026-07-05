package com.mentormatch.model.enums;

import com.elo.denuncia.CriterioDenuncia;

public enum CriterioDenunciaMentorMatch implements CriterioDenuncia {
    CONTEUDO_INADEQUADO,
    INFORMACAO_FALSA,
    COMPORTAMENTO_INADEQUADO,
    FRAUDE,
    USO_INDEVIDO,
    OUTRO;

    @Override public String codigo() { return name(); }
}
