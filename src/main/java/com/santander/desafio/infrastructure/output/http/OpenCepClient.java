package com.santander.desafio.infrastructure.output.http;

import com.santander.desafio.application.port.output.CepProvider;
import com.santander.desafio.domain.exception.CepNaoEncontradoException;
import com.santander.desafio.domain.exception.IntegracaoCepException;
import com.santander.desafio.domain.exception.TimeoutCepException;
import com.santander.desafio.domain.model.Endereco;
import com.santander.desafio.infrastructure.output.http.dto.OpenCepResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;

@Component
public class OpenCepClient implements CepProvider {

    private final RestClient restClient;

    public OpenCepClient(
            @Qualifier("openCepRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }

    @Override
    public Endereco consultar(String cep) {
        try {
            OpenCepResponse response = restClient
                    .get()
                    .uri("/v1/{cep}", cep)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            (request, httpResponse) -> {
                                throw new CepNaoEncontradoException(cep);
                            }
                    )
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            (request, httpResponse) -> {
                                throw new IntegracaoCepException(
                                        cep,
                                        httpResponse.getStatusCode().value(),
                                        "O provedor de CEP rejeitou a requisição."
                                );
                            }
                    )
                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            (request, httpResponse) -> {
                                throw new IntegracaoCepException(
                                        cep,
                                        httpResponse.getStatusCode().value(),
                                        "O provedor de CEP está indisponível."
                                );
                            }
                    )
                    .body(OpenCepResponse.class);

            if (response == null) {
                throw new IntegracaoCepException(
                        cep,
                        "O provedor de CEP retornou uma resposta vazia."
                );
            }

            return response.toDomain();

        } catch (CepNaoEncontradoException exception) {
            throw exception;

        } catch (IntegracaoCepException exception) {

            throw exception;

        } catch (ResourceAccessException exception) {
            if (causaEhTimeout(exception)) {
                throw new TimeoutCepException(
                        cep,
                        "O tempo limite para consultar o provedor de CEP foi excedido.",
                        exception
                );
            }

            throw new IntegracaoCepException(
                    cep,
                    "Não foi possível acessar o provedor de CEP.",
                    exception
            );

        } catch (RestClientResponseException exception) {
            throw new IntegracaoCepException(
                    cep,
                    exception.getStatusCode().value(),
                    "Erro HTTP ao consultar o provedor de CEP.",
                    exception
            );

        } catch (Exception exception) {
            throw new IntegracaoCepException(
                    cep,
                    "Ocorreu um erro inesperado ao consultar o provedor de CEP.",
                    exception
            );
        }
    }

    private boolean causaEhTimeout(Throwable exception) {
        Throwable causaAtual = exception;

        while (causaAtual != null) {
            if (causaAtual instanceof SocketTimeoutException
                    || causaAtual instanceof HttpTimeoutException) {
                return true;
            }

            causaAtual = causaAtual.getCause();
        }

        return false;
    }
}