package com.santander.desafio.infrastructure.output.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.desafio.application.model.PaginaResultado;
import com.santander.desafio.application.port.output.ConsultaRepository;
import com.santander.desafio.domain.model.ConsultaCep;
import com.santander.desafio.domain.model.Endereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ConsultaRepositoryAdapter implements ConsultaRepository {


    private final ConsultaJpaRepository consultaJpaRepository;
    private final ObjectMapper objectMapper;

    public ConsultaRepositoryAdapter(
            ConsultaJpaRepository consultaJpaRepository,
            ObjectMapper objectMapper
    ) {
        this.consultaJpaRepository = consultaJpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ConsultaCep salvar(ConsultaCep consulta) {
        if (consulta == null) {
            throw new IllegalArgumentException(
                    "A consulta não pode ser nula."
            );
        }

        ConsultaJpaEntity entity = toEntity(consulta);
        ConsultaJpaEntity entitySalva = consultaJpaRepository.save(entity);

        return toDomain(entitySalva);
    }

    private ConsultaJpaEntity toEntity(ConsultaCep consulta) {
        return new ConsultaJpaEntity(
                consulta.getId(),
                consulta.getCepConsultado(),
                consulta.getConsultadoEm(),
                consulta.getStatus(),
                consulta.getHttpStatus(),
                serializarEndereco(consulta.getEndereco()),
                consulta.getMensagemErro(),
                consulta.getDuracaoMs()
        );
    }

    private ConsultaCep toDomain(ConsultaJpaEntity entity) {
        return switch (entity.getStatus()) {
            case SUCESSO -> ConsultaCep.sucesso(
                    entity.getId(),
                    entity.getCepConsultado(),
                    entity.getConsultadoEm(),
                    desserializarEndereco(entity.getResposta()),
                    entity.getHttpStatus(),
                    entity.getDuracaoMs()
            );

            case NAO_ENCONTRADO -> ConsultaCep.naoEncontrado(
                    entity.getId(),
                    entity.getCepConsultado(),
                    entity.getConsultadoEm(),
                    entity.getHttpStatus(),
                    entity.getMensagemErro(),
                    entity.getDuracaoMs()
            );

            case ERRO_INTEGRACAO -> ConsultaCep.erroIntegracao(
                    entity.getId(),
                    entity.getCepConsultado(),
                    entity.getConsultadoEm(),
                    entity.getHttpStatus(),
                    entity.getMensagemErro(),
                    entity.getDuracaoMs()
            );
        };
    }

    private String serializarEndereco(Endereco endereco) {
        if (endereco == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(endereco);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Não foi possível serializar o endereço.",
                    exception
            );
        }
    }

    private Endereco desserializarEndereco(String resposta) {
        if (resposta == null || resposta.isBlank()) {
            throw new IllegalStateException(
                    "Uma consulta de sucesso deve possuir uma resposta."
            );
        }

        try {
            return objectMapper.readValue(
                    resposta,
                    Endereco.class
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Não foi possível desserializar o endereço.",
                    exception
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResultado<ConsultaCep> buscarHistorico(
            int pagina,
            int tamanho
    ) {
        PageRequest pageRequest = PageRequest.of(
                pagina,
                tamanho,
                Sort.by(
                        Sort.Direction.DESC,
                        "consultadoEm"
                )
        );

        Page<ConsultaJpaEntity> resultado =
                consultaJpaRepository.findAll(pageRequest);

        return new PaginaResultado<>(
                resultado.getContent()
                        .stream()
                        .map(this::toDomain)
                        .toList(),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.isFirst(),
                resultado.isLast()
        );
    }
}