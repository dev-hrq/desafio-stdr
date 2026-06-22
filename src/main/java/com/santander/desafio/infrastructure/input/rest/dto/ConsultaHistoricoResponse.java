package com.santander.desafio.infrastructure.input.rest.dto;

import com.santander.desafio.domain.model.ConsultaCep;
import com.santander.desafio.domain.model.StatusConsulta;

import java.time.Instant;
import java.util.UUID;

public record ConsultaHistoricoResponse(
        UUID id,
        String cepConsultado,
        Instant consultadoEm,
        StatusConsulta status,
        Integer httpStatus,
        EnderecoResponse resposta,
        String mensagemErro,
        Long duracaoMs
) {

    public static ConsultaHistoricoResponse from(
            ConsultaCep consulta
    ) {
        EnderecoResponse enderecoResponse =
                consulta.getEndereco() == null
                        ? null
                        : EnderecoResponse.from(
                        consulta.getEndereco()
                );

        return new ConsultaHistoricoResponse(
                consulta.getId(),
                consulta.getCepConsultado(),
                consulta.getConsultadoEm(),
                consulta.getStatus(),
                consulta.getHttpStatus(),
                enderecoResponse,
                consulta.getMensagemErro(),
                consulta.getDuracaoMs()
        );
    }
}