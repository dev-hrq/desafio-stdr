package com.santander.desafio.domain.model;

import com.santander.desafio.domain.model.ConsultaCep;
import com.santander.desafio.domain.model.Endereco;
import com.santander.desafio.domain.model.StatusConsulta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsultaCepTest {

    @Test
    void deveCriarConsultaComSucesso() {
        Endereco endereco = new Endereco(
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

        ConsultaCep consulta = ConsultaCep.sucesso(
                "15050305",
                endereco,
                200,
                30L
        );

        assertNotNull(consulta.getId());
        assertNotNull(consulta.getConsultadoEm());
        assertEquals(StatusConsulta.SUCESSO, consulta.getStatus());
        assertEquals("15050305", consulta.getCepConsultado());
        assertEquals(200, consulta.getHttpStatus());
        assertEquals(endereco, consulta.getEndereco());
        assertNull(consulta.getMensagemErro());
        assertTrue(consulta.foiBemSucedida());
    }

    @Test
    void deveCriarConsultaDeCepNaoEncontrado() {
        ConsultaCep consulta = ConsultaCep.naoEncontrado(
                "00000000",
                404,
                "CEP não encontrado.",
                25L
        );

        assertEquals(
                StatusConsulta.NAO_ENCONTRADO,
                consulta.getStatus()
        );
        assertNull(consulta.getEndereco());
        assertEquals("CEP não encontrado.", consulta.getMensagemErro());
        assertTrue(consulta.naoFoiEncontrada());
    }

    @Test
    void deveCriarConsultaComErroDeIntegracao() {
        ConsultaCep consulta = ConsultaCep.erroIntegracao(
                "99999999",
                500,
                "Erro no provedor de CEP.",
                50L
        );

        assertEquals(
                StatusConsulta.ERRO_INTEGRACAO,
                consulta.getStatus()
        );
        assertNull(consulta.getEndereco());
        assertTrue(consulta.houveErroDeIntegracao());
    }

    @Test
    void deveRejeitarCepNaoNormalizado() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ConsultaCep.naoEncontrado(
                        "15050-305",
                        404,
                        "CEP não encontrado.",
                        10L
                )
        );
    }
}