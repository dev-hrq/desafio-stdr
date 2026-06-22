package com.santander.desafio.infrastructure.output.http.dto;

import com.santander.desafio.domain.model.Endereco;

public record OpenCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String unidade,
        String bairro,
        String localidade,
        String uf,
        String estado,
        String regiao,
        String ibge
) {

    public Endereco toDomain() {
        return new Endereco(
                cep,
                logradouro,
                complemento,
                unidade,
                bairro,
                localidade,
                uf,
                estado,
                regiao,
                ibge
        );
    }
}