package com.studybuddy.exception;

import com.elo.web.EloRestExceptionHandler;
import com.elo.web.dto.ErroResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends EloRestExceptionHandler {

    @ExceptionHandler(ModeracaoGrupoEstudoInvalidaException.class)
    public ResponseEntity<ErroResponseDTO> handleModeracaoInvalida(ModeracaoGrupoEstudoInvalidaException ex) {
        return erro(HttpStatus.BAD_REQUEST, "MODERACAO_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(PerfilAcademicoAusenteException.class)
    public ResponseEntity<ErroResponseDTO> handlePerfilAcademicoAusente(PerfilAcademicoAusenteException ex) {
        return erro(HttpStatus.UNPROCESSABLE_ENTITY, "PERFIL_ACADEMICO_AUSENTE", ex.getMessage());
    }

    @ExceptionHandler(GroqIntegracaoException.class)
    public ResponseEntity<ErroResponseDTO> handleGroqIntegracao(GroqIntegracaoException ex) {
        return erro(HttpStatus.SERVICE_UNAVAILABLE, "GROQ_INDISPONIVEL", ex.getMessage());
    }
}
