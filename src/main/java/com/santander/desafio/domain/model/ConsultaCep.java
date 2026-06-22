package com.santander.desafio.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ConsultaCep {

    private final UUID id;
    private final String cepConsultado;
    private final Instant consultadoEm;
    private final StatusConsulta status;
    private final Integer httpStatus;
    private final Endereco endereco;
    private final String mensagemErro;
    private final Long duracaoMs;

    private ConsultaCep(
            UUID id,
            String cepConsultado,
            Instant consultadoEm,
            StatusConsulta status,
            Integer httpStatus,
            Endereco endereco,
            String mensagemErro,
            Long duracaoMs
    ) {
        this.id = Objects.requireNonNull(id, "O id não pode ser nulo.");
        this.cepConsultado = validarCepConsultado(cepConsultado);
        this.consultadoEm = Objects.requireNonNull(
                consultadoEm,
                "A data da consulta não pode ser nula."
        );
        this.status = Objects.requireNonNull(
                status,
                "O status da consulta não pode ser nulo."
        );
        this.httpStatus = httpStatus;
        this.endereco = endereco;
        this.mensagemErro = mensagemErro;
        this.duracaoMs = validarDuracao(duracaoMs);
    }

    public static ConsultaCep sucesso(
            UUID id,
            String cepConsultado,
            Instant consultadoEm,
            Endereco endereco,
            int httpStatus,
            long duracaoMs
    ) {
        Objects.requireNonNull(
                endereco,
                "O endereço não pode ser nulo em uma consulta bem-sucedida."
        );

        return new ConsultaCep(
                id,
                cepConsultado,
                consultadoEm,
                StatusConsulta.SUCESSO,
                httpStatus,
                endereco,
                null,
                duracaoMs
        );
    }

    public static ConsultaCep sucesso(
            String cepConsultado,
            Endereco endereco,
            int httpStatus,
            long duracaoMs
    ) {
        return sucesso(
                UUID.randomUUID(),
                cepConsultado,
                Instant.now(),
                endereco,
                httpStatus,
                duracaoMs
        );
    }

    public static ConsultaCep naoEncontrado(
            UUID id,
            String cepConsultado,
            Instant consultadoEm,
            int httpStatus,
            String mensagemErro,
            long duracaoMs
    ) {
        return new ConsultaCep(
                id,
                cepConsultado,
                consultadoEm,
                StatusConsulta.NAO_ENCONTRADO,
                httpStatus,
                null,
                mensagemErro,
                duracaoMs
        );
    }

    public static ConsultaCep naoEncontrado(
            String cepConsultado,
            int httpStatus,
            String mensagemErro,
            long duracaoMs
    ) {
        return naoEncontrado(
                UUID.randomUUID(),
                cepConsultado,
                Instant.now(),
                httpStatus,
                mensagemErro,
                duracaoMs
        );
    }


    public static ConsultaCep erroIntegracao(
            UUID id,
            String cepConsultado,
            Instant consultadoEm,
            Integer httpStatus,
            String mensagemErro,
            long duracaoMs
    ) {
        return new ConsultaCep(
                id,
                cepConsultado,
                consultadoEm,
                StatusConsulta.ERRO_INTEGRACAO,
                httpStatus,
                null,
                mensagemErro,
                duracaoMs
        );
    }

    public static ConsultaCep erroIntegracao(
            String cepConsultado,
            Integer httpStatus,
            String mensagemErro,
            long duracaoMs
    ) {
        return erroIntegracao(
                UUID.randomUUID(),
                cepConsultado,
                Instant.now(),
                httpStatus,
                mensagemErro,
                duracaoMs
        );
    }

    private static String validarCepConsultado(String cepConsultado) {
        if (cepConsultado == null || !cepConsultado.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "O CEP consultado deve possuir exatamente 8 números."
            );
        }

        return cepConsultado;
    }

    private static Long validarDuracao(Long duracaoMs) {
        if (duracaoMs != null && duracaoMs < 0) {
            throw new IllegalArgumentException(
                    "A duração da consulta não pode ser negativa."
            );
        }

        return duracaoMs;
    }

    public UUID getId() {
        return id;
    }

    public String getCepConsultado() {
        return cepConsultado;
    }

    public Instant getConsultadoEm() {
        return consultadoEm;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public Long getDuracaoMs() {
        return duracaoMs;
    }

    public boolean foiBemSucedida() {
        return status == StatusConsulta.SUCESSO;
    }

    public boolean naoFoiEncontrada() {
        return status == StatusConsulta.NAO_ENCONTRADO;
    }

    public boolean houveErroDeIntegracao() {
        return status == StatusConsulta.ERRO_INTEGRACAO;
    }

    public static long calcularDuracaoEmMilissegundos(Instant inicio) {
        Objects.requireNonNull(inicio, "O instante inicial não pode ser nulo.");

        return Duration.between(inicio, Instant.now()).toMillis();
    }
}
