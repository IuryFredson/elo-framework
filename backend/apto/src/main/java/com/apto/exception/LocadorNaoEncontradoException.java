package com.apto.exception;


import com.elo.exception.EntidadeNaoEncontradaException;
public class LocadorNaoEncontradoException extends EntidadeNaoEncontradaException {
    public LocadorNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}