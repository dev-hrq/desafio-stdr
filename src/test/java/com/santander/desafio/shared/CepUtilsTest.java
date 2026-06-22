package com.santander.desafio.shared;

import com.santander.desafio.domain.exception.CepInvalidoException;
import com.santander.desafio.shared.CepUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CepUtilsTest {

    @Test
    void deveRetornarCepSemAlteracaoQuandoJaEstiverNormalizado() {
        String resultado = CepUtils.normalizar("15050305");

        assertEquals("15050305", resultado);
    }

    @Test
    void deveRemoverHifenDoCepFormatado() {
        String resultado = CepUtils.normalizar("15050-305");

        assertEquals("15050305", resultado);
    }

    @Test
    void deveRemoverEspacosExternos() {
        String resultado = CepUtils.normalizar("  15050-305  ");

        assertEquals("15050305", resultado);
    }

    @Test
    void deveLancarExcecaoQuandoCepForNulo() {
        CepInvalidoException exception = assertThrows(
                CepInvalidoException.class,
                () -> CepUtils.normalizar(null)
        );

        assertEquals(
                "O CEP deve ser informado.",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoCepEstiverVazio() {
        assertThrows(
                CepInvalidoException.class,
                () -> CepUtils.normalizar("")
        );
    }

    @Test
    void deveLancarExcecaoQuandoCepContiverSomenteEspacos() {
        assertThrows(
                CepInvalidoException.class,
                () -> CepUtils.normalizar("   ")
        );
    }

    @Test
    void deveLancarExcecaoQuandoCepTiverMenosDeOitoNumeros() {
        assertThrows(
                CepInvalidoException.class,
                () -> CepUtils.normalizar("1505030")
        );
    }

    @Test
    void deveLancarExcecaoQuandoCepTiverMaisDeOitoNumeros() {
        assertThrows(
                CepInvalidoException.class,
                () -> CepUtils.normalizar("150503050")
        );
    }

    @Test
    void deveLancarExcecaoQuandoCepContiverLetras() {
        assertThrows(
                CepInvalidoException.class,
                () -> CepUtils.normalizar("15050ABC")
        );
    }

    @Test
    void deveLancarExcecaoQuandoCepMisturarLetrasENumeros() {
        assertThrows(
                CepInvalidoException.class,
                () -> CepUtils.normalizar("15abc050305")
        );
    }

    @Test
    void deveLancarExcecaoQuandoHifenEstiverNaPosicaoErrada() {
        assertThrows(
                CepInvalidoException.class,
                () -> CepUtils.normalizar("150-50305")
        );
    }

    @Test
    void deveLancarExcecaoQuandoExistiremEspacosInternos() {
        assertThrows(
                CepInvalidoException.class,
                () -> CepUtils.normalizar("15050 305")
        );
    }
}