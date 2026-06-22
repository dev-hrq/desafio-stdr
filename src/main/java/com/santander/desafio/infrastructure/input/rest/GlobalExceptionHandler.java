package com.santander.desafio.infrastructure.input.rest;

import com.santander.desafio.domain.exception.CepInvalidoException;
import com.santander.desafio.domain.exception.CepNaoEncontradoException;
import com.santander.desafio.domain.exception.IntegracaoCepException;
import com.santander.desafio.infrastructure.input.rest.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<ApiErrorResponse> tratarCepInvalido(
            CepInvalidoException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> tratarCepNaoEncontrado(
            CepNaoEncontradoException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(IntegracaoCepException.class)
    public ResponseEntity<ApiErrorResponse> tratarErroIntegracao(
            IntegracaoCepException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarErroInesperado(
            Exception exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado.",
                request.getRequestURI()
        );
    }

    private ResponseEntity<ApiErrorResponse> criarResposta(
            HttpStatus status,
            String message,
            String path
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}