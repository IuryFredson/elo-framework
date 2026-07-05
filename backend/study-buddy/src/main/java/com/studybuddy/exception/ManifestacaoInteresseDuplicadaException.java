package com.studybuddy.exception;


import com.elo.exception.RegraNegocioException;
public class ManifestacaoInteresseDuplicadaException extends RegraNegocioException {

    public ManifestacaoInteresseDuplicadaException(String message) {
        super(message);
    }
}
