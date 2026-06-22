package com.santander.desafio.application.service;

import com.santander.desafio.application.port.output.CepProvider;
import com.santander.desafio.application.port.output.ConsultaRepository;
import com.santander.desafio.application.service.ConsultarCepService;
import com.santander.desafio.domain.exception.CepInvalidoException;
import com.santander.desafio.domain.exception.CepNaoEncontradoException;
import com.santander.desafio.domain.exception.IntegracaoCepException;
import com.santander.desafio.domain.model.ConsultaCep;
import com.santander.desafio.domain.model.Endereco;
import com.santander.desafio.domain.model.StatusConsulta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultarCepServiceTest {

    private CepProvider cepProvider;
    private ConsultaRepository consultaRepository;
    private ConsultarCepService consultarCepService;

    @BeforeEach
    void setUp() {
        cepProvider = mock(CepProvider.class);
        consultaRepository = mock(ConsultaRepository.class);

        consultarCepService = new ConsultarCepService(
                cepProvider,
                consultaRepository
        );
    }

    @Test
    void deveConsultarCepESalvarRegistroDeSucesso() {
        Endereco endereco = criarEndereco();

        when(cepProvider.consultar("15050305"))
                .thenReturn(endereco);

        Endereco resultado = consultarCepService.consultar("15050-305");

        assertSame(endereco, resultado);

        verify(cepProvider).consultar("15050305");

        ArgumentCaptor<ConsultaCep> captor =
                ArgumentCaptor.forClass(ConsultaCep.class);

        verify(consultaRepository).salvar(captor.capture());

        ConsultaCep consultaSalva = captor.getValue();

        assertEquals("15050305", consultaSalva.getCepConsultado());
        assertEquals(StatusConsulta.SUCESSO, consultaSalva.getStatus());
        assertEquals(200, consultaSalva.getHttpStatus());
        assertEquals(endereco, consultaSalva.getEndereco());
        assertNull(consultaSalva.getMensagemErro());
    }

    @Test
    void deveSalvarRegistroQuandoCepNaoForEncontrado() {
        CepNaoEncontradoException exception =
                new CepNaoEncontradoException("00000000");

        when(cepProvider.consultar("00000000"))
                .thenThrow(exception);

        CepNaoEncontradoException resultado = assertThrows(
                CepNaoEncontradoException.class,
                () -> consultarCepService.consultar("00000000")
        );

        assertSame(exception, resultado);

        ArgumentCaptor<ConsultaCep> captor =
                ArgumentCaptor.forClass(ConsultaCep.class);

        verify(consultaRepository).salvar(captor.capture());

        ConsultaCep consultaSalva = captor.getValue();

        assertEquals("00000000", consultaSalva.getCepConsultado());
        assertEquals(
                StatusConsulta.NAO_ENCONTRADO,
                consultaSalva.getStatus()
        );
        assertEquals(404, consultaSalva.getHttpStatus());
        assertNull(consultaSalva.getEndereco());
        assertEquals(
                "CEP não encontrado: 00000000",
                consultaSalva.getMensagemErro()
        );
    }

    @Test
    void deveSalvarRegistroQuandoOcorrerErroDeIntegracao() {
        IntegracaoCepException exception =
                new IntegracaoCepException(
                        "99999999",
                        500,
                        "O provedor de CEP está indisponível."
                );

        when(cepProvider.consultar("99999999"))
                .thenThrow(exception);

        IntegracaoCepException resultado = assertThrows(
                IntegracaoCepException.class,
                () -> consultarCepService.consultar("99999999")
        );

        assertSame(exception, resultado);

        ArgumentCaptor<ConsultaCep> captor =
                ArgumentCaptor.forClass(ConsultaCep.class);

        verify(consultaRepository).salvar(captor.capture());

        ConsultaCep consultaSalva = captor.getValue();

        assertEquals("99999999", consultaSalva.getCepConsultado());
        assertEquals(
                StatusConsulta.ERRO_INTEGRACAO,
                consultaSalva.getStatus()
        );
        assertEquals(500, consultaSalva.getHttpStatus());
        assertNull(consultaSalva.getEndereco());
        assertEquals(
                "O provedor de CEP está indisponível.",
                consultaSalva.getMensagemErro()
        );
    }

    @Test
    void deveSalvarErroDeIntegracaoSemStatusHttp() {
        IntegracaoCepException exception =
                new IntegracaoCepException(
                        "99999999",
                        "Não foi possível acessar o provedor de CEP."
                );

        when(cepProvider.consultar("99999999"))
                .thenThrow(exception);

        assertThrows(
                IntegracaoCepException.class,
                () -> consultarCepService.consultar("99999999")
        );

        ArgumentCaptor<ConsultaCep> captor =
                ArgumentCaptor.forClass(ConsultaCep.class);

        verify(consultaRepository).salvar(captor.capture());

        ConsultaCep consultaSalva = captor.getValue();

        assertEquals(
                StatusConsulta.ERRO_INTEGRACAO,
                consultaSalva.getStatus()
        );
        assertNull(consultaSalva.getHttpStatus());
    }

    @Test
    void naoDeveConsultarProvedorNemSalvarQuandoCepForInvalido() {
        assertThrows(
                CepInvalidoException.class,
                () -> consultarCepService.consultar("123")
        );

        verifyNoInteractions(cepProvider);
        verifyNoInteractions(consultaRepository);
    }

    @Test
    void deveNormalizarCepAntesDeConsultarOProvedor() {
        Endereco endereco = criarEndereco();

        when(cepProvider.consultar("15050305"))
                .thenReturn(endereco);

        consultarCepService.consultar(" 15050-305 ");

        verify(cepProvider).consultar("15050305");
        verify(consultaRepository).salvar(any(ConsultaCep.class));
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