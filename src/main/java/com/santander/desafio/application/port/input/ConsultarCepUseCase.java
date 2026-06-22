package com.santander.desafio.application.port.input;


import com.santander.desafio.domain.model.Endereco;

public interface ConsultarCepUseCase {

    Endereco consultar(String cep);
}
