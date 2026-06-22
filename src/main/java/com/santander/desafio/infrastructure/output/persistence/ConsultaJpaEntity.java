package com.santander.desafio.infrastructure.output.persistence;

import com.santander.desafio.domain.model.StatusConsulta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consultas_cep")
public class ConsultaJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(
            name = "cep_consultado",
            nullable = false,
            length = 8
    )
    private String cepConsultado;

    @Column(
            name = "consultado_em",
            nullable = false,
            updatable = false
    )
    private Instant consultadoEm;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private StatusConsulta status;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(
            name = "resposta",
            columnDefinition = "TEXT"
    )
    private String resposta;

    @Column(
            name = "mensagem_erro",
            columnDefinition = "TEXT"
    )
    private String mensagemErro;

    @Column(
            name = "duracao_ms",
            nullable = false
    )
    private Long duracaoMs;

    protected ConsultaJpaEntity() {
        // Construtor obrigatório para o JPA.
    }

    public ConsultaJpaEntity(
            UUID id,
            String cepConsultado,
            Instant consultadoEm,
            StatusConsulta status,
            Integer httpStatus,
            String resposta,
            String mensagemErro,
            Long duracaoMs
    ) {
        this.id = id;
        this.cepConsultado = cepConsultado;
        this.consultadoEm = consultadoEm;
        this.status = status;
        this.httpStatus = httpStatus;
        this.resposta = resposta;
        this.mensagemErro = mensagemErro;
        this.duracaoMs = duracaoMs;
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

    public String getResposta() {
        return resposta;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public Long getDuracaoMs() {
        return duracaoMs;
    }
}