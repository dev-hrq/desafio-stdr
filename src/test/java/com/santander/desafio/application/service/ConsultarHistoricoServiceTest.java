package com.santander.desafio.application.service;

import com.santander.desafio.application.model.PaginaResultado;
import com.santander.desafio.application.port.output.ConsultaRepository;
import com.santander.desafio.domain.model.ConsultaCep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultarHistoricoServiceTest {

    private ConsultaRepository consultaRepository;
    private ConsultarHistoricoService service;

    @BeforeEach
    void setUp() {
        consultaRepository = mock(ConsultaRepository.class);

        service = new ConsultarHistoricoService(
                consultaRepository
        );
    }

    @Test
    void deveBuscarHistoricoPaginado() {
        PaginaResultado<ConsultaCep> pagina =
                new PaginaResultado<>(
                        List.of(),
                        0,
                        10,
                        0,
                        0,
                        true,
                        true
                );

        when(consultaRepository.buscarHistorico(0, 10))
                .thenReturn(pagina);

        PaginaResultado<ConsultaCep> resultado =
                service.consultar(0, 10);

        assertSame(pagina, resultado);

        verify(consultaRepository)
                .buscarHistorico(0, 10);
    }

    @Test
    void deveRejeitarPaginaNegativa() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.consultar(-1, 10)
        );

        assertEquals(
                "O número da página não pode ser negativo.",
                exception.getMessage()
        );
    }

    @Test
    void deveRejeitarTamanhoIgualAZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.consultar(0, 0)
        );
    }

    @Test
    void deveRejeitarTamanhoMaiorQueCem() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.consultar(0, 101)
        );
    }
}