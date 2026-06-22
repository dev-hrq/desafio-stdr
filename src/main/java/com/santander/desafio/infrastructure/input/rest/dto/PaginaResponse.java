package com.santander.desafio.infrastructure.input.rest.dto;

import com.santander.desafio.application.model.PaginaResultado;

import java.util.List;
import java.util.function.Function;

public record PaginaResponse<T>(
        List<T> conteudo,
        int paginaAtual,
        int tamanhoPagina,
        long totalElementos,
        int totalPaginas,
        boolean primeiraPagina,
        boolean ultimaPagina
) {

    public static <D, R> PaginaResponse<R> from(
            PaginaResultado<D> pagina,
            Function<D, R> mapper
    ) {
        List<R> conteudo = pagina.conteudo()
                .stream()
                .map(mapper)
                .toList();

        return new PaginaResponse<>(
                conteudo,
                pagina.paginaAtual(),
                pagina.tamanhoPagina(),
                pagina.totalElementos(),
                pagina.totalPaginas(),
                pagina.primeiraPagina(),
                pagina.ultimaPagina()
        );
    }
}