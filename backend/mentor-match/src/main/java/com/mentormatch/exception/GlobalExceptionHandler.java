package com.mentormatch.exception;

import com.elo.web.EloRestExceptionHandler;
import com.elo.web.dto.ErroResponseDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler extends EloRestExceptionHandler {
    @ExceptionHandler(MentorMatchException.class)
    public ResponseEntity<ErroResponseDTO> handleDominio(MentorMatchException e) {
        return erro(HttpStatus.BAD_REQUEST, "REGRA_MENTOR_MATCH", e.getMessage());
    }
    @ExceptionHandler(GroqIntegracaoException.class)
    public ResponseEntity<ErroResponseDTO> handleGroq(GroqIntegracaoException e) {
        return erro(HttpStatus.SERVICE_UNAVAILABLE, "GROQ_INDISPONIVEL", e.getMessage());
    }
}
