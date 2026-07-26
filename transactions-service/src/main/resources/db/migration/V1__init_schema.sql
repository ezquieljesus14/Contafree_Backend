-- V1: Schema inicial del módulo de transacciones

CREATE TABLE categories (
    id                  UUID         NOT NULL DEFAULT gen_random_uuid(),
    name                VARCHAR(100) NOT NULL,
    type                VARCHAR(10)  NOT NULL,
    debit_account_code  VARCHAR(10)  NOT NULL,
    credit_account_code VARCHAR(10)  NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_categories_name UNIQUE (name),
    CONSTRAINT ck_categories_type CHECK (type IN ('INCOME', 'EXPENSE'))
);

CREATE TABLE transactions (
    id               UUID           NOT NULL DEFAULT gen_random_uuid(),
    user_id          UUID           NOT NULL,
    type             VARCHAR(10)    NOT NULL,
    amount           NUMERIC(12, 2) NOT NULL,
    description      VARCHAR(255)   NOT NULL,
    date             DATE           NOT NULL,
    category_id      UUID           NOT NULL,
    contact_id       UUID,
    status           VARCHAR(10)    NOT NULL DEFAULT 'PENDING',
    journal_entry_id UUID,
    idempotency_key  VARCHAR(36)    UNIQUE,
    deleted          BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP,
    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT ck_transactions_type CHECK (type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT ck_transactions_status CHECK (status IN ('COMPLETED', 'PENDING')),
    CONSTRAINT ck_transactions_amount CHECK (amount > 0)
);

CREATE INDEX idx_transactions_user ON transactions (user_id);
CREATE INDEX idx_transactions_category ON transactions (category_id);
CREATE INDEX idx_transactions_date ON transactions (date);
CREATE INDEX idx_transactions_status ON transactions (status);
CREATE INDEX idx_transactions_deleted ON transactions (deleted);
