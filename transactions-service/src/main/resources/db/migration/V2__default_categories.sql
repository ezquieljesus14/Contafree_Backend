-- V2: Categorías por defecto con códigos PGC España

INSERT INTO categories (id, name, type, debit_account_code, credit_account_code, created_at) VALUES
-- INCOME: débito 572 (Bancos), crédito cuenta de ingreso
(gen_random_uuid(), 'Servicios prestados',   'INCOME',  '572', '705', NOW()),
(gen_random_uuid(), 'Venta de productos',    'INCOME',  '572', '700', NOW()),
(gen_random_uuid(), 'Otros ingresos',        'INCOME',  '572', '759', NOW()),

-- EXPENSE: débito cuenta de gasto, crédito 572 (Bancos)
(gen_random_uuid(), 'Compras',               'EXPENSE', '600', '572', NOW()),
(gen_random_uuid(), 'Suministros',           'EXPENSE', '628', '572', NOW()),
(gen_random_uuid(), 'Servicios externos',    'EXPENSE', '623', '572', NOW()),
(gen_random_uuid(), 'Gastos de personal',    'EXPENSE', '640', '572', NOW()),
(gen_random_uuid(), 'Arrendamientos',        'EXPENSE', '621', '572', NOW());
