package com.santander.desafio.infrastructure.output.http;

import com.santander.desafio.domain.exception.CepNaoEncontradoException;
import com.santander.desafio.domain.exception.IntegracaoCepException;
import com.santander.desafio.domain.model.Endereco;
import com.santander.desafio.infrastructure.output.http.OpenCepClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OpenCepClientTest {

    private MockRestServiceServer mockServer;
    private OpenCepClient openCepClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("http://localhost");

        mockServer = MockRestServiceServer
                .bindTo(builder)
                .build();

        openCepClient = new OpenCepClient(builder.build());
    }

    @Test
    void deveConsultarCepComSucesso() {
        String responseBody = """
                {
                  "cep": "15050-305",
                  "logradouro": "Rua Josina Teixeira de Carvalho",
                  "complemento": "",
                  "unidade": "",
                  "bairro": "Vila Anchieta",
                  "localidade": "São José do Rio Preto",
                  "uf": "SP",
                  "estado": "São Paulo",
                  "regiao": "Sudeste",
                  "ibge": "3549805"
                }
                """;

        mockServer.expect(
                        once(),
                        requestTo("http://localhost/v1/15050305")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess(responseBody, MediaType.APPLICATION_JSON)
                );

        Endereco endereco = openCepClient.consultar("15050305");

        assertEquals("15050-305", endereco.cep());
        assertEquals(
                "Rua Josina Teixeira de Carvalho",
                endereco.logradouro()
        );
        assertEquals("Vila Anchieta", endereco.bairro());
        assertEquals("São José do Rio Preto", endereco.localidade());
        assertEquals("SP", endereco.uf());
        assertEquals("3549805", endereco.ibge());

        mockServer.verify();
    }

    @Test
    void deveLancarExcecaoQuandoCepNaoForEncontrado() {
        mockServer.expect(
                        once(),
                        requestTo("http://localhost/v1/00000000")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        CepNaoEncontradoException exception = assertThrows(
                CepNaoEncontradoException.class,
                () -> openCepClient.consultar("00000000")
        );

        assertEquals("00000000", exception.getCep());

        mockServer.verify();
    }

    @Test
    void deveLancarExcecaoQuandoProvedorRetornarErroInterno() {
        mockServer.expect(
                        once(),
                        requestTo("http://localhost/v1/99999999")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        IntegracaoCepException exception = assertThrows(
                IntegracaoCepException.class,
                () -> openCepClient.consultar("99999999")
        );

        assertEquals("99999999", exception.getCep());
        assertEquals(500, exception.getHttpStatus());

        mockServer.verify();
    }

    @Test
    void deveLancarExcecaoQuandoProvedorRejeitarRequisicao() {
        mockServer.expect(
                        once(),
                        requestTo("http://localhost/v1/11111111")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

        IntegracaoCepException exception = assertThrows(
                IntegracaoCepException.class,
                () -> openCepClient.consultar("11111111")
        );

        assertEquals("11111111", exception.getCep());
        assertEquals(400, exception.getHttpStatus());

        mockServer.verify();
    }

    @Test
    void deveLancarExcecaoQuandoRespostaEstiverVazia() {
        mockServer.expect(
                        once(),
                        requestTo("http://localhost/v1/22222222")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess("", MediaType.APPLICATION_JSON)
                );

        IntegracaoCepException exception = assertThrows(
                IntegracaoCepException.class,
                () -> openCepClient.consultar("22222222")
        );

        assertEquals("22222222", exception.getCep());
        assertEquals(
                "O provedor de CEP retornou uma resposta vazia.",
                exception.getMessage()
        );

        mockServer.verify();
    }
}