package com.santander.desafio.infrastructure.input.rest;

import com.santander.desafio.application.model.PaginaResultado;
import com.santander.desafio.application.port.input.ConsultarHistoricoUseCase;
import com.santander.desafio.domain.model.ConsultaCep;
import com.santander.desafio.infrastructure.input.rest.dto.ConsultaHistoricoResponse;
import com.santander.desafio.infrastructure.input.rest.dto.PaginaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/consultas")
public class ConsultaController {

    private final ConsultarHistoricoUseCase consultarHistoricoUseCase;

    public ConsultaController(
            ConsultarHistoricoUseCase consultarHistoricoUseCase
    ) {
        this.consultarHistoricoUseCase =
                consultarHistoricoUseCase;
    }

    @GetMapping
    public ResponseEntity<
            PaginaResponse<ConsultaHistoricoResponse>
            > consultarHistorico(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaResultado<ConsultaCep> resultado =
                consultarHistoricoUseCase.consultar(
                        page,
                        size
                );

        PaginaResponse<ConsultaHistoricoResponse> response =
                PaginaResponse.from(
                        resultado,
                        ConsultaHistoricoResponse::from
                );

        return ResponseEntity.ok(response);
    }

}