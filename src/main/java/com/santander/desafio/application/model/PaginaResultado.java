package com.santander.desafio.application.model;

import java.util.List;

public record PaginaResultado<T>(
        List<T> conteudo,
        int paginaAtual,
        int tamanhoPagina,
        long totalElementos,
        int totalPaginas,
        boolean primeiraPagina,
        boolean ultimaPagina
) {

    public PaginaResultado {
        conteudo = List.copyOf(conteudo);
    }
}