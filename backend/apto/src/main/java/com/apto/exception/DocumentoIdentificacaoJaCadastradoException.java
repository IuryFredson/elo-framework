package com.apto.exception;


import com.elo.exception.RegraNegocioException;
public class DocumentoIdentificacaoJaCadastradoException extends RegraNegocioException {
    public DocumentoIdentificacaoJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}