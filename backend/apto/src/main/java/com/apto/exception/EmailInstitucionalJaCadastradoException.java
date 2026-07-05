package com.apto.exception;


import com.elo.exception.RegraNegocioException;
public class EmailInstitucionalJaCadastradoException extends RegraNegocioException {
    public EmailInstitucionalJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}