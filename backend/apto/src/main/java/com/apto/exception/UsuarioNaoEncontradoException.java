package com.apto.exception;


import com.elo.exception.EntidadeNaoEncontradaException;
public class UsuarioNaoEncontradoException extends EntidadeNaoEncontradaException {
    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}