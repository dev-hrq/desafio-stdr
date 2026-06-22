package com.santander.desafio.application.service;

import com.santander.desafio.application.model.PaginaResultado;
import com.santander.desafio.application.port.input.ConsultarHistoricoUseCase;
import com.santander.desafio.application.port.output.ConsultaRepository;
import com.santander.desafio.domain.model.ConsultaCep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarHistoricoService
        implements ConsultarHistoricoUseCase {

    private static final int TAMANHO_MAXIMO = 100;

    private final ConsultaRepository consultaRepository;

    public ConsultarHistoricoService(
            ConsultaRepository consultaRepository
    ) {
        this.consultaRepository = consultaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResultado<ConsultaCep> consultar(
            int pagina,
            int tamanho
    ) {
        validarPaginacao(pagina, tamanho);

        return consultaRepository.buscarHistorico(
                pagina,
                tamanho
        );
    }

    private void validarPaginacao(int pagina, int tamanho) {
        if (pagina < 0) {
            throw new IllegalArgumentException(
                    "O número da página não pode ser negativo."
            );
        }

        if (tamanho < 1 || tamanho > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException(
                    "O tamanho da página deve estar entre 1 e 100."
            );
        }
    }
}