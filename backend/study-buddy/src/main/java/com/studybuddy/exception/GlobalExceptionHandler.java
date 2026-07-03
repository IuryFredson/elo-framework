package com.studybuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EstudanteNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleEstudanteNaoEncontrado(EstudanteNaoEncontradoException ex) {
        return erro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(GrupoEstudoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleGrupoNaoEncontrado(GrupoEstudoNaoEncontradoException ex) {
        return erro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ManifestacaoInteresseNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handleManifestacaoNaoEncontrada(ManifestacaoInteresseNaoEncontradaException ex) {
        return erro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DenunciaGrupoEstudoNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handleDenunciaNaoEncontrada(DenunciaGrupoEstudoNaoEncontradaException ex) {
        return erro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String, String>> handleAcessoNegado(AcessoNegadoException ex) {
        return erro(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(GrupoEstudoInativoException.class)
    public ResponseEntity<Map<String, String>> handleGrupoInativo(GrupoEstudoInativoException ex) {
        return erro(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ManifestacaoInteresseDuplicadaException.class)
    public ResponseEntity<Map<String, String>> handleManifestacaoDuplicada(ManifestacaoInteresseDuplicadaException ex) {
        return erro(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ManifestacaoInteresseInvalidaException.class)
    public ResponseEntity<Map<String, String>> handleManifestacaoInvalida(ManifestacaoInteresseInvalidaException ex) {
        return erro(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TransicaoInvalidaManifestacaoException.class)
    public ResponseEntity<Map<String, String>> handleTransicaoManifestacao(TransicaoInvalidaManifestacaoException ex) {
        return erro(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TransicaoInvalidaDenunciaException.class)
    public ResponseEntity<Map<String, String>> handleTransicaoDenuncia(TransicaoInvalidaDenunciaException ex) {
        return erro(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ModeracaoGrupoEstudoInvalidaException.class)
    public ResponseEntity<Map<String, String>> handleModeracaoInvalida(ModeracaoGrupoEstudoInvalidaException ex) {
        return erro(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> erros.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(erros);
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", mensagem);
        return ResponseEntity.status(status).body(erro);
    }
}
