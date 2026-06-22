package com.santander.desafio.infrastructure.output.persistence;

import com.santander.desafio.domain.model.ConsultaCep;
import com.santander.desafio.domain.model.Endereco;
import com.santander.desafio.domain.model.StatusConsulta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultaRepositoryAdapterTest {

    private ConsultaJpaRepository consultaJpaRepository;
    private ConsultaRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        consultaJpaRepository =
                mock(ConsultaJpaRepository.class);

        ObjectMapper objectMapper = new ObjectMapper();

        adapter = new ConsultaRepositoryAdapter(
                consultaJpaRepository,
                objectMapper
        );

        when(consultaJpaRepository.save(any(ConsultaJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void deveSalvarConsultaComSucesso() {
        Endereco endereco = criarEndereco();

        ConsultaCep consulta = ConsultaCep.sucesso(
                "15050305",
                endereco,
                200,
                35L
        );

        ConsultaCep resultado = adapter.salvar(consulta);

        ArgumentCaptor<ConsultaJpaEntity> captor =
                ArgumentCaptor.forClass(ConsultaJpaEntity.class);

        verify(consultaJpaRepository).save(captor.capture());

        ConsultaJpaEntity entity = captor.getValue();

        assertEquals(consulta.getId(), entity.getId());
        assertEquals("15050305", entity.getCepConsultado());
        assertEquals(StatusConsulta.SUCESSO, entity.getStatus());
        assertEquals(200, entity.getHttpStatus());
        assertEquals(35L, entity.getDuracaoMs());
        assertNotNull(entity.getResposta());
        assertNull(entity.getMensagemErro());

        assertEquals(consulta.getId(), resultado.getId());
        assertEquals(StatusConsulta.SUCESSO, resultado.getStatus());
        assertEquals(endereco, resultado.getEndereco());
    }

    @Test
    void deveSalvarConsultaDeCepNaoEncontrado() {
        ConsultaCep consulta = ConsultaCep.naoEncontrado(
                "00000000",
                404,
                "CEP não encontrado.",
                20L
        );

        ConsultaCep resultado = adapter.salvar(consulta);

        ArgumentCaptor<ConsultaJpaEntity> captor =
                ArgumentCaptor.forClass(ConsultaJpaEntity.class);

        verify(consultaJpaRepository).save(captor.capture());

        ConsultaJpaEntity entity = captor.getValue();

        assertEquals(
                StatusConsulta.NAO_ENCONTRADO,
                entity.getStatus()
        );
        assertEquals(404, entity.getHttpStatus());
        assertNull(entity.getResposta());
        assertEquals(
                "CEP não encontrado.",
                entity.getMensagemErro()
        );

        assertEquals(
                StatusConsulta.NAO_ENCONTRADO,
                resultado.getStatus()
        );
        assertNull(resultado.getEndereco());
    }

    @Test
    void deveSalvarErroDeIntegracao() {
        ConsultaCep consulta = ConsultaCep.erroIntegracao(
                "99999999",
                500,
                "O provedor está indisponível.",
                40L
        );

        ConsultaCep resultado = adapter.salvar(consulta);

        ArgumentCaptor<ConsultaJpaEntity> captor =
                ArgumentCaptor.forClass(ConsultaJpaEntity.class);

        verify(consultaJpaRepository).save(captor.capture());

        ConsultaJpaEntity entity = captor.getValue();

        assertEquals(
                StatusConsulta.ERRO_INTEGRACAO,
                entity.getStatus()
        );
        assertEquals(500, entity.getHttpStatus());
        assertNull(entity.getResposta());
        assertEquals(
                "O provedor está indisponível.",
                entity.getMensagemErro()
        );

        assertEquals(
                StatusConsulta.ERRO_INTEGRACAO,
                resultado.getStatus()
        );
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