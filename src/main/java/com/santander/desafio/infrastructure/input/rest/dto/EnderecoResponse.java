package com.santander.desafio.infrastructure.input.rest.dto;

import com.santander.desafio.domain.model.Endereco;

public record EnderecoResponse(
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

    public static EnderecoResponse from(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException(
                    "O endereço não pode ser nulo."
            );
        }

        return new EnderecoResponse(
                endereco.cep(),
                endereco.logradouro(),
                endereco.complemento(),
                endereco.unidade(),
                endereco.bairro(),
                endereco.localidade(),
                endereco.uf(),
                endereco.estado(),
                endereco.regiao(),
                endereco.ibge()
        );
    }
}