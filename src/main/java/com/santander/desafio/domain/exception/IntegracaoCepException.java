package com.santander.desafio.domain.exception;

public class IntegracaoCepException extends RuntimeException {

    private final String cep;
    private final Integer httpStatus;

    public IntegracaoCepException(
            String cep,
            String message
    ) {
        this(cep, null, message, null);
    }

    public IntegracaoCepException(
            String cep,
            String message,
            Throwable cause
    ) {
        this(cep, null, message, cause);
    }

    public IntegracaoCepException(
            String cep,
            Integer httpStatus,
            String message
    ) {
        this(cep, httpStatus, message, null);
    }

    public IntegracaoCepException(
            String cep,
            Integer httpStatus,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.cep = cep;
        this.httpStatus = httpStatus;
    }

    public String getCep() {
        return cep;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }
}