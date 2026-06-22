package com.santander.desafio.application.port.output;

import com.santander.desafio.domain.model.ConsultaCep;

public interface ConsultaRepository {

    ConsultaCep salvar(ConsultaCep consulta);

    // todo: remover

//    List<ConsultaCep> buscarTodas();
//
//    Optional<ConsultaCep> buscarPorId(UUID id);
}