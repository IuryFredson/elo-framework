package com.studybuddy.exception;


import com.elo.exception.RegraNegocioException;
public class EmailJaCadastradoException extends RegraNegocioException {

    public EmailJaCadastradoException(String message) {
        super(message);
    }
}
