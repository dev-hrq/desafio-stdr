package com.santander.desafio.infrastructure.input.rest;

import com.santander.desafio.application.port.input.ConsultarCepUseCase;
import com.santander.desafio.domain.exception.CepInvalidoException;
import com.santander.desafio.domain.exception.CepNaoEncontradoException;
import com.santander.desafio.domain.exception.IntegracaoCepException;
import com.santander.desafio.domain.model.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CepControllerTest {

    private ConsultarCepUseCase consultarCepUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        consultarCepUseCase = mock(ConsultarCepUseCase.class);

        CepController controller =
                new CepController(consultarCepUseCase);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveRetornarEnderecoQuandoCepForEncontrado()
            throws Exception {

        Endereco endereco = criarEndereco();

        when(consultarCepUseCase.consultar("15050305"))
                .thenReturn(endereco);

        mockMvc.perform(
                        get("/api/v1/ceps/15050305")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep")
                        .value("15050-305"))
                .andExpect(jsonPath("$.logradouro")
                        .value("Rua Josina Teixeira de Carvalho"))
                .andExpect(jsonPath("$.bairro")
                        .value("Vila Anchieta"))
                .andExpect(jsonPath("$.localidade")
                        .value("São José do Rio Preto"))
                .andExpect(jsonPath("$.uf")
                        .value("SP"))
                .andExpect(jsonPath("$.ibge")
                        .value("3549805"));
    }

    @Test
    void deveRetornarBadRequestQuandoCepForInvalido()
            throws Exception {

        when(consultarCepUseCase.consultar("123"))
                .thenThrow(
                        new CepInvalidoException(
                                "O CEP deve estar no formato 00000000 ou 00000-000."
                        )
                );

        mockMvc.perform(
                        get("/api/v1/ceps/123")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "O CEP deve estar no formato 00000000 ou 00000-000."
                        ))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/ceps/123"));
    }

    @Test
    void deveRetornarNotFoundQuandoCepNaoExistir()
            throws Exception {

        when(consultarCepUseCase.consultar("00000000"))
                .thenThrow(
                        new CepNaoEncontradoException("00000000")
                );

        mockMvc.perform(
                        get("/api/v1/ceps/00000000")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("CEP não encontrado: 00000000"));
    }

    @Test
    void deveRetornarServiceUnavailableQuandoProvedorFalhar()
            throws Exception {

        when(consultarCepUseCase.consultar("99999999"))
                .thenThrow(
                        new IntegracaoCepException(
                                "99999999",
                                500,
                                "O provedor de CEP está indisponível."
                        )
                );

        mockMvc.perform(
                        get("/api/v1/ceps/99999999")
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error")
                        .value("Service Unavailable"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "O provedor de CEP está indisponível."
                        ));
    }

    private Endereco criarEndereco() {
        return new Endereco(
                "15050-305",
                "Rua Josina Teixeira de Carvalho",
                "",
                "",
                "Vila Anchieta",
                "São José do Rio Preto",
                "SP",
                "São Paulo",
                "Sudeste",
                "3549805"
        );
    }
}