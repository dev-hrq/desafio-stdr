package com.santander.desafio.shared;

import com.santander.desafio.domain.exception.CepInvalidoException;

public final class CepUtils {

    private static final String CEP_SEM_FORMATACAO = "\\d{8}";
    private static final String CEP_COM_FORMATACAO = "\\d{5}-\\d{3}";

    private CepUtils() {
        // Impede a instanciação da classe utilitária.
    }

    public static String normalizar(String cep) {
        validarPreenchimento(cep);

        String cepSemEspacosExternos = cep.trim();

        validarFormato(cepSemEspacosExternos);

        return cepSemEspacosExternos.replace("-", "");
    }

    private static void validarPreenchimento(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new CepInvalidoException(
                    "O CEP deve ser informado."
            );
        }
    }

    private static void validarFormato(String cep) {
        boolean semFormatacao = cep.matches(CEP_SEM_FORMATACAO);
        boolean comFormatacao = cep.matches(CEP_COM_FORMATACAO);

        if (!semFormatacao && !comFormatacao) {
            throw new CepInvalidoException(
                    "O CEP deve estar no formato 00000000 ou 00000-000."
            );
        }
    }
}