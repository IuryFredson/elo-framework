package com.apto.exception;

import com.elo.web.EloRestExceptionHandler;
import com.elo.web.dto.ErroResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends EloRestExceptionHandler {

    @ExceptionHandler(ModeracaoInvalidaException.class)
    public ResponseEntity<ErroResponseDTO> handleModeracaoInvalida(ModeracaoInvalidaException ex) {
        return erro(HttpStatus.BAD_REQUEST, "MODERACAO_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(AvaliacaoInvalidaException.class)
    public ResponseEntity<ErroResponseDTO> handleAvaliacaoInvalida(AvaliacaoInvalidaException ex) {
        return erro(HttpStatus.BAD_REQUEST, "AVALIACAO_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(PerfilConvivenciaAusenteException.class)
    public ResponseEntity<ErroResponseDTO> handlePerfilConvivenciaAusente(PerfilConvivenciaAusenteException ex) {
        return erro(HttpStatus.UNPROCESSABLE_ENTITY, "PERFIL_CONVIVENCIA_AUSENTE", ex.getMessage());
    }

    @ExceptionHandler(GroqIntegracaoException.class)
    public ResponseEntity<ErroResponseDTO> handleGroqIntegracao(GroqIntegracaoException ex) {
        return erro(HttpStatus.SERVICE_UNAVAILABLE, "GROQ_INDISPONIVEL", ex.getMessage());
    }
}
