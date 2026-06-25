package com.apto.exception;

public class LocadorNaoEncontradoException extends RuntimeException {
    public LocadorNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}