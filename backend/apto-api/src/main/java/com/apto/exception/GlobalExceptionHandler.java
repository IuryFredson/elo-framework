package com.apto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(LocadorNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleLocadorNaoEncontrado(LocadorNaoEncontradoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Map<String, String>> handleEmailJaCadastrado(EmailJaCadastradoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(EmailInstitucionalJaCadastradoException.class)
    public ResponseEntity<Map<String, String>> handleEmailInstitucionalJaCadastrado(EmailInstitucionalJaCadastradoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(DocumentoIdentificacaoJaCadastradoException.class)
    public ResponseEntity<Map<String, String>> handleDocumentoIdentificacaoJaCadastrado(DocumentoIdentificacaoJaCadastradoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                erros.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(MoradiaNaoEncontradaException.class)
    public ResponseEntity<Map<String,String>> handleMoradiaNaoEncontrada(MoradiaNaoEncontradaException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(AnuncioNaoEncontradoException.class)
    public ResponseEntity<Map<String,String>> handleAnuncioNaoEncontrado(AnuncioNaoEncontradoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String,String>> handleAcessoNegado(AcessoNegadoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(MoradiaAssociadaComAnuncioException.class)
    public ResponseEntity<Map<String,String>> handleMoradiaAssociadaComAnuncio(MoradiaAssociadaComAnuncioException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(ManifestacaoInteresseNaoEncontradaException.class)
    public ResponseEntity<Map<String,String>> handleManifestacaoInteresseNaoEncontrada(ManifestacaoInteresseNaoEncontradaException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(DenunciaNaoEncontradaException.class)
    public ResponseEntity<Map<String,String>> handleDenunciaNaoEncontrada(DenunciaNaoEncontradaException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(ManifestacaoInteresseDuplicadaException.class)
    public ResponseEntity<Map<String,String>> handleManifestacaoInteresseDuplicada(ManifestacaoInteresseDuplicadaException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(ManifestacaoInteresseInvalidaException.class)
    public ResponseEntity<Map<String,String>> handleManifestacaoInteresseInvalida(ManifestacaoInteresseInvalidaException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(TransicaoInvalidaManifestacaoException.class)
    public ResponseEntity<Map<String,String>> handleTransicaoInvalidaManifestacao(TransicaoInvalidaManifestacaoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(AnuncioNaoAtivoException.class)
    public ResponseEntity<Map<String,String>> handleAnuncioNaoAtivo(AnuncioNaoAtivoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(TransicaoInvalidaStatusException.class)
    public ResponseEntity<Map<String,String>> handleTransicaoInvalidaStatus(TransicaoInvalidaStatusException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(ModeracaoInvalidaException.class)
    public ResponseEntity<Map<String,String>> handleModeracaoInvalida(ModeracaoInvalidaException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(GroqIntegracaoException.class)
    public ResponseEntity<Map<String,String>> handleGroqIntegracao(GroqIntegracaoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(erro);
    }
    @ExceptionHandler(PerfilConvivenciaAusenteException.class)
    public ResponseEntity<Map<String, String>> handlePerfilConvivenciaAusente(PerfilConvivenciaAusenteException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    @ExceptionHandler(AvaliacaoNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handleAvaliacaoNaoEncontrada(AvaliacaoNaoEncontradaException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(AvaliacaoDuplicadaException.class)
    public ResponseEntity<Map<String, String>> handleAvaliacaoDuplicada(AvaliacaoDuplicadaException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(AvaliacaoInvalidaException.class)
    public ResponseEntity<Map<String, String>> handleAvaliacaoInvalida(AvaliacaoInvalidaException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(AnuncianteNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleAnuncianteNaoEncontrado(
            AnuncianteNaoEncontradoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
}
