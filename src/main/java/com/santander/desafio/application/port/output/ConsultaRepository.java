package com.santander.desafio.application.port.output;

import com.santander.desafio.application.model.PaginaResultado;
import com.santander.desafio.domain.model.ConsultaCep;

public interface ConsultaRepository {

    ConsultaCep salvar(ConsultaCep consulta);

    PaginaResultado<ConsultaCep> buscarHistorico(
            int pagina,
            int tamanho
    );
}