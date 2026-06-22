package com.santander.desafio.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsultaJpaRepository
        extends JpaRepository<ConsultaJpaEntity, UUID> {
}