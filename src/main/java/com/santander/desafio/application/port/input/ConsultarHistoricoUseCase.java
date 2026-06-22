package com.santander.desafio.application.port.input;

import com.santander.desafio.application.model.PaginaResultado;
import com.santander.desafio.domain.model.ConsultaCep;

public interface ConsultarHistoricoUseCase {

    PaginaResultado<ConsultaCep> consultar(
            int pagina,
            int tamanho
    );
}