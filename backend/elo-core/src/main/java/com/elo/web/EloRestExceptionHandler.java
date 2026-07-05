package com.elo.web;

import com.elo.exception.AcessoNegadoFrameworkException;
import com.elo.exception.EntidadeNaoEncontradaException;
import com.elo.exception.RegraNegocioException;
import com.elo.exception.TransicaoInvalidaException;
import com.elo.web.dto.ErroResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class EloRestExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponseDTO> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex) {
        return erro(HttpStatus.NOT_FOUND, "ENTIDADE_NAO_ENCONTRADA", ex.getMessage());
    }

    @ExceptionHandler(AcessoNegadoFrameworkException.class)
    public ResponseEntity<ErroResponseDTO> handleAcessoNegado(AcessoNegadoFrameworkException ex) {
        return erro(HttpStatus.FORBIDDEN, "ACESSO_NEGADO", ex.getMessage());
    }

    @ExceptionHandler(TransicaoInvalidaException.class)
    public ResponseEntity<ErroResponseDTO> handleTransicaoInvalida(TransicaoInvalidaException ex) {
        return erro(HttpStatus.CONFLICT, "TRANSICAO_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponseDTO> handleRegraNegocio(RegraNegocioException ex) {
        return erro(HttpStatus.CONFLICT, "REGRA_NEGOCIO", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                erros.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(erros);
    }

    protected ResponseEntity<ErroResponseDTO> erro(HttpStatus status, String codigo, String mensagem) {
        return ResponseEntity
                .status(status)
                .body(new ErroResponseDTO(mensagem, codigo, status.value()));
    }
}
