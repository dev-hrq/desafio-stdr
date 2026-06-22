package com.santander.desafio.application.service;

import com.santander.desafio.application.port.input.ConsultarCepUseCase;
import com.santander.desafio.application.port.output.CepProvider;
import com.santander.desafio.application.port.output.ConsultaRepository;
import com.santander.desafio.domain.exception.CepNaoEncontradoException;
import com.santander.desafio.domain.exception.IntegracaoCepException;
import com.santander.desafio.domain.model.ConsultaCep;
import com.santander.desafio.domain.model.Endereco;
import org.springframework.stereotype.Service;
import com.santander.desafio.shared.CepUtils;

import java.time.Duration;
import java.time.Instant;

@Service
public class ConsultarCepService implements ConsultarCepUseCase {

    private final CepProvider cepProvider;
    private final ConsultaRepository consultaRepository;

    public ConsultarCepService(
            CepProvider cepProvider,
            ConsultaRepository consultaRepository
    ) {
        this.cepProvider = cepProvider;
        this.consultaRepository = consultaRepository;
    }

    @Override
    public Endereco consultar(String cep) {
        String cepNormalizado = CepUtils.normalizar(cep);
        Instant inicio = Instant.now();

        try {
            Endereco endereco = cepProvider.consultar(cepNormalizado);

            long duracaoMs = calcularDuracao(inicio);

            ConsultaCep consulta = ConsultaCep.sucesso(
                    cepNormalizado,
                    endereco,
                    200,
                    duracaoMs
            );

            consultaRepository.salvar(consulta);

            return endereco;

        } catch (CepNaoEncontradoException exception) {
            long duracaoMs = calcularDuracao(inicio);

            ConsultaCep consulta = ConsultaCep.naoEncontrado(
                    cepNormalizado,
                    404,
                    exception.getMessage(),
                    duracaoMs
            );

            consultaRepository.salvar(consulta);

            throw exception;

        } catch (IntegracaoCepException exception) {
            long duracaoMs = calcularDuracao(inicio);

            ConsultaCep consulta = ConsultaCep.erroIntegracao(
                    cepNormalizado,
                    exception.getHttpStatus(),
                    exception.getMessage(),
                    duracaoMs
            );

            consultaRepository.salvar(consulta);

            throw exception;
        }
    }

    private long calcularDuracao(Instant inicio) {
        return Duration.between(inicio, Instant.now()).toMillis();
    }
}