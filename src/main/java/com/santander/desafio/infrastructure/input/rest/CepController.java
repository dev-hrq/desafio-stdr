package com.santander.desafio.infrastructure.input.rest;

import com.santander.desafio.application.port.input.ConsultarCepUseCase;
import com.santander.desafio.domain.model.Endereco;
import com.santander.desafio.infrastructure.input.rest.dto.EnderecoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ceps")
public class CepController {

    private final ConsultarCepUseCase consultarCepUseCase;

    public CepController(ConsultarCepUseCase consultarCepUseCase) {
        this.consultarCepUseCase = consultarCepUseCase;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<EnderecoResponse> consultar(
            @PathVariable String cep
    ) {
        Endereco endereco = consultarCepUseCase.consultar(cep);

        return ResponseEntity.ok(
                EnderecoResponse.from(endereco)
        );
    }
}