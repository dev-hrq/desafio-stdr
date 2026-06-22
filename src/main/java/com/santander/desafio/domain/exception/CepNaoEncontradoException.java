package com.santander.desafio.domain.exception;

public class CepNaoEncontradoException extends RuntimeException {

    private final String cep;

    public CepNaoEncontradoException(String cep) {
        super("CEP não encontrado: " + cep);
        this.cep = cep;
    }

    public CepNaoEncontradoException(String cep, String message) {
        super(message);
        this.cep = cep;
    }

    public CepNaoEncontradoException(
            String cep,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.cep = cep;
    }

    public String getCep() {
        return cep;
    }
}
