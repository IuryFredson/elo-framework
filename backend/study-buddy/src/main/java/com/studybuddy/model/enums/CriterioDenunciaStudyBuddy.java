package com.studybuddy.model.enums;

import com.elo.denuncia.CriterioDenuncia;

public enum CriterioDenunciaStudyBuddy implements CriterioDenuncia {
    GRUPO_INADEQUADO,
    CONTEUDO_FORA_DO_TEMA,
    COMPORTAMENTO_INADEQUADO,
    USO_INDEVIDO,
    OUTRO;

    @Override
    public String codigo() {
        return name();
    }
}
