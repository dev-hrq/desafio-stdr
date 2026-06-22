package com.santander.desafio.infrastructure.input.rest;

import com.santander.desafio.domain.exception.CepInvalidoException;
import com.santander.desafio.domain.exception.CepNaoEncontradoException;
import com.santander.desafio.domain.exception.IntegracaoCepException;
import com.santander.desafio.domain.exception.TimeoutCepException;
import com.santander.desafio.infrastructure.input.rest.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<ApiErrorResponse> tratarCepInvalido(
            CepInvalidoException exception,
            HttpServletRequest request
    ) {
        LOGGER.warn(
                "CEP inválido. path={}, mensagem={}",
                request.getRequestURI(),
                exception.getMessage()
        );

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "CEP_INVALIDO",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> tratarCepNaoEncontrado(
            CepNaoEncontradoException exception,
            HttpServletRequest request
    ) {
        LOGGER.info(
                "CEP não encontrado. cep={}, path={}",
                exception.getCep(),
                request.getRequestURI()
        );

        return criarResposta(
                HttpStatus.NOT_FOUND,
                "CEP_NAO_ENCONTRADO",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(TimeoutCepException.class)
    public ResponseEntity<ApiErrorResponse> tratarTimeout(
            TimeoutCepException exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Timeout ao consultar provedor. cep={}, path={}",
                exception.getCep(),
                request.getRequestURI(),
                exception
        );

        return criarResposta(
                HttpStatus.GATEWAY_TIMEOUT,
                "TIMEOUT_PROVEDOR_CEP",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(IntegracaoCepException.class)
    public ResponseEntity<ApiErrorResponse> tratarErroIntegracao(
            IntegracaoCepException exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Erro de integração com provedor. cep={}, statusExterno={}, path={}",
                exception.getCep(),
                exception.getHttpStatus(),
                request.getRequestURI(),
                exception
        );

        return criarResposta(
                HttpStatus.SERVICE_UNAVAILABLE,
                "PROVEDOR_CEP_INDISPONIVEL",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> tratarMetodoNaoPermitido(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        LOGGER.warn(
                "Método HTTP não permitido. metodo={}, path={}",
                exception.getMethod(),
                request.getRequestURI()
        );

        return criarResposta(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METODO_NAO_PERMITIDO",
                "O método HTTP utilizado não é permitido para este recurso.",
                request
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> tratarErroDePersistencia(
            DataAccessException exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Erro ao acessar o banco de dados. path={}",
                request.getRequestURI(),
                exception
        );

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERRO_PERSISTENCIA",
                "Não foi possível registrar a consulta.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarErroInesperado(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Erro inesperado na aplicação. path={}",
                request.getRequestURI(),
                exception
        );

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERRO_INTERNO",
                "Ocorreu um erro interno inesperado.",
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> criarResposta(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> tratarArgumentoInvalido(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        LOGGER.warn(
                "Parâmetro inválido. path={}, mensagem={}",
                request.getRequestURI(),
                exception.getMessage()
        );

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "PARAMETRO_INVALIDO",
                exception.getMessage(),
                request
        );
    }
}