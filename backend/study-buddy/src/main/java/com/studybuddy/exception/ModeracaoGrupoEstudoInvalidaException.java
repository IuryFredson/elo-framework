package com.studybuddy.exception;


import com.elo.exception.RegraNegocioException;
public class ModeracaoGrupoEstudoInvalidaException extends RegraNegocioException {

    public ModeracaoGrupoEstudoInvalidaException(String message) {
        super(message);
    }
}
