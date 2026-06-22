package com.santander.desafio.domain.exception;

public class TimeoutCepException extends IntegracaoCepException {

    public TimeoutCepException(
            String cep,
            String message,
            Throwable cause
    ) {
        super(cep, null, message, cause);
    }

    public TimeoutCepException(
            String cep,
            String message
    ) {
        super(cep, null, message, null);
    }
}