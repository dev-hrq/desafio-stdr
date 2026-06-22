CREATE TABLE consultas_cep (
                               id UUID PRIMARY KEY,
                               cep_consultado VARCHAR(8) NOT NULL,
                               consultado_em TIMESTAMP WITH TIME ZONE NOT NULL,
                               status VARCHAR(30) NOT NULL,
                               http_status INTEGER,
                               resposta TEXT,
                               mensagem_erro TEXT,
                               duracao_ms BIGINT NOT NULL,

                               CONSTRAINT chk_consultas_cep_duracao
                                   CHECK (duracao_ms >= 0),

                               CONSTRAINT chk_consultas_cep_status
                                   CHECK (
                                       status IN (
                                                  'SUCESSO',
                                                  'NAO_ENCONTRADO',
                                                  'ERRO_INTEGRACAO'
                                           )
                                       )
);

CREATE INDEX idx_consultas_cep_cep
    ON consultas_cep (cep_consultado);

CREATE INDEX idx_consultas_cep_consultado_em
    ON consultas_cep (consultado_em DESC);

CREATE INDEX idx_consultas_cep_status
    ON consultas_cep (status);