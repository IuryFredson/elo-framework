package com.apto.exception;


import com.elo.exception.RegraNegocioException;
public class EmailJaCadastradoException extends RegraNegocioException {
    public EmailJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}