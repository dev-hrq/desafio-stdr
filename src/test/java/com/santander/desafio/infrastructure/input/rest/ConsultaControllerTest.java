package com.santander.desafio.infrastructure.input.rest;

import com.santander.desafio.application.model.PaginaResultado;
import com.santander.desafio.application.port.input.ConsultarHistoricoUseCase;
import com.santander.desafio.domain.model.ConsultaCep;
import com.santander.desafio.domain.model.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConsultaControllerTest {

    private ConsultarHistoricoUseCase useCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        useCase = mock(ConsultarHistoricoUseCase.class);

        ConsultaController controller =
                new ConsultaController(useCase);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }

    @Test
    void deveRetornarHistoricoPaginado()
            throws Exception {

        ConsultaCep consulta = ConsultaCep.sucesso(
                "15050305",
                criarEndereco(),
                200,
                30L
        );

        PaginaResultado<ConsultaCep> pagina =
                new PaginaResultado<>(
                        List.of(consulta),
                        0,
                        10,
                        1,
                        1,
                        true,
                        true
                );

        when(useCase.consultar(0, 10))
                .thenReturn(pagina);

        mockMvc.perform(
                        get("/api/v1/consultas")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paginaAtual")
                        .value(0))
                .andExpect(jsonPath("$.tamanhoPagina")
                        .value(10))
                .andExpect(jsonPath("$.totalElementos")
                        .value(1))
                .andExpect(jsonPath("$.totalPaginas")
                        .value(1))
                .andExpect(jsonPath("$.primeiraPagina")
                        .value(true))
                .andExpect(jsonPath("$.ultimaPagina")
                        .value(true))
                .andExpect(jsonPath("$.conteudo[0].cepConsultado")
                        .value("15050305"))
                .andExpect(jsonPath("$.conteudo[0].status")
                        .value("SUCESSO"))
                .andExpect(jsonPath("$.conteudo[0].httpStatus")
                        .value(200))
                .andExpect(jsonPath("$.conteudo[0].resposta.cep")
                        .value("15050-305"));

        verify(useCase).consultar(0, 10);
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