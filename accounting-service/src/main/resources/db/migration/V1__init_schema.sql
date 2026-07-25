-- V1: Schema inicial del módulo de contabilidad

CREATE TABLE chart_of_accounts (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    code        VARCHAR(20)  NOT NULL,
    name        VARCHAR(255) NOT NULL,
    type        VARCHAR(20)  NOT NULL,
    parent_code VARCHAR(20),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_chart_of_accounts PRIMARY KEY (id),
    CONSTRAINT uq_chart_of_accounts_code UNIQUE (code),
    CONSTRAINT ck_chart_of_accounts_type CHECK (type IN ('ASSET','LIABILITY','EQUITY','REVENUE','EXPENSE'))
);

CREATE TABLE journals (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    start_date  DATE         NOT NULL,
    end_date    DATE         NOT NULL,
    status      VARCHAR(10)  NOT NULL DEFAULT 'OPEN',
    created_by  UUID         NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    closed_at   TIMESTAMP,
    CONSTRAINT pk_journals PRIMARY KEY (id),
    CONSTRAINT ck_journals_status CHECK (status IN ('OPEN','CLOSED'))
);

CREATE TABLE journal_entries (
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    journal_id      UUID         NOT NULL,
    description     VARCHAR(500) NOT NULL,
    entry_date      DATE         NOT NULL,
    status          VARCHAR(10)  NOT NULL DEFAULT 'DRAFT',
    idempotency_key VARCHAR(255) NOT NULL,
    created_by      UUID         NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    CONSTRAINT pk_journal_entries PRIMARY KEY (id),
    CONSTRAINT uq_journal_entries_idempotency UNIQUE (idempotency_key),
    CONSTRAINT fk_journal_entries_journal FOREIGN KEY (journal_id) REFERENCES journals (id),
    CONSTRAINT ck_journal_entries_status CHECK (status IN ('DRAFT','POSTED'))
);

CREATE TABLE journal_lines (
    id               UUID           NOT NULL DEFAULT gen_random_uuid(),
    journal_entry_id UUID           NOT NULL,
    account_code     VARCHAR(20)    NOT NULL,
    type             VARCHAR(6)     NOT NULL,
    amount           NUMERIC(19, 2) NOT NULL,
    CONSTRAINT pk_journal_lines PRIMARY KEY (id),
    CONSTRAINT fk_journal_lines_entry FOREIGN KEY (journal_entry_id) REFERENCES journal_entries (id),
    CONSTRAINT fk_journal_lines_account FOREIGN KEY (account_code) REFERENCES chart_of_accounts (code),
    CONSTRAINT ck_journal_lines_type CHECK (type IN ('DEBIT','CREDIT')),
    CONSTRAINT ck_journal_lines_amount CHECK (amount > 0)
);

CREATE INDEX idx_journal_entries_journal ON journal_entries (journal_id);
CREATE INDEX idx_journal_lines_entry ON journal_lines (journal_entry_id);
CREATE INDEX idx_journal_lines_account ON journal_lines (account_code);
