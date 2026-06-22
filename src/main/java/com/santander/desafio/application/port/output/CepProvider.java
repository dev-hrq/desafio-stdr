package com.santander.desafio.application.port.output;

import com.santander.desafio.domain.model.Endereco;

public interface CepProvider {

    Endereco consultar(String cep);
}
