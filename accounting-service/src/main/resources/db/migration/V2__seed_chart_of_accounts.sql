-- ============================================================
-- Plan General Contable (PGC) España — cuentas principales
-- ============================================================

INSERT INTO chart_of_accounts (id, code, name, type, parent_code, active) VALUES

-- ============================================================
-- GRUPO 1 — FINANCIACIÓN BÁSICA (EQUITY / LIABILITY)
-- ============================================================
(gen_random_uuid(), '1',    'Financiación básica',                              'EQUITY',    NULL,  true),
(gen_random_uuid(), '10',   'Capital',                                           'EQUITY',    '1',   true),
(gen_random_uuid(), '100',  'Capital social',                                    'EQUITY',    '10',  true),
(gen_random_uuid(), '101',  'Fondo social',                                      'EQUITY',    '10',  true),
(gen_random_uuid(), '102',  'Capital',                                           'EQUITY',    '10',  true),
(gen_random_uuid(), '11',   'Reservas y otros instrumentos de patrimonio',       'EQUITY',    '1',   true),
(gen_random_uuid(), '110',  'Prima de emisión o asunción',                       'EQUITY',    '11',  true),
(gen_random_uuid(), '112',  'Reserva legal',                                     'EQUITY',    '11',  true),
(gen_random_uuid(), '113',  'Reservas voluntarias',                              'EQUITY',    '11',  true),
(gen_random_uuid(), '12',   'Resultados pendientes de aplicación',               'EQUITY',    '1',   true),
(gen_random_uuid(), '120',  'Remanente',                                         'EQUITY',    '12',  true),
(gen_random_uuid(), '121',  'Resultados negativos de ejercicios anteriores',     'EQUITY',    '12',  true),
(gen_random_uuid(), '129',  'Resultado del ejercicio',                           'EQUITY',    '12',  true),
(gen_random_uuid(), '14',   'Provisiones',                                       'LIABILITY', '1',   true),
(gen_random_uuid(), '141',  'Provisión para impuestos',                          'LIABILITY', '14',  true),
(gen_random_uuid(), '142',  'Provisión para otras responsabilidades',            'LIABILITY', '14',  true),
(gen_random_uuid(), '17',   'Deudas a largo plazo',                              'LIABILITY', '1',   true),
(gen_random_uuid(), '170',  'Deudas a largo plazo con entidades de crédito',     'LIABILITY', '17',  true),
(gen_random_uuid(), '172',  'Deudas a largo plazo transformables en subv.',      'LIABILITY', '17',  true),
(gen_random_uuid(), '173',  'Proveedores de inmovilizado a largo plazo',         'LIABILITY', '17',  true),

-- ============================================================
-- GRUPO 2 — ACTIVO NO CORRIENTE (ASSET)
-- ============================================================
(gen_random_uuid(), '2',    'Activo no corriente',                               'ASSET',     NULL,  true),
(gen_random_uuid(), '20',   'Inmovilizaciones intangibles',                      'ASSET',     '2',   true),
(gen_random_uuid(), '200',  'Investigación',                                     'ASSET',     '20',  true),
(gen_random_uuid(), '201',  'Desarrollo',                                        'ASSET',     '20',  true),
(gen_random_uuid(), '203',  'Propiedad industrial',                              'ASSET',     '20',  true),
(gen_random_uuid(), '205',  'Derechos de traspaso',                              'ASSET',     '20',  true),
(gen_random_uuid(), '206',  'Aplicaciones informáticas',                         'ASSET',     '20',  true),
(gen_random_uuid(), '21',   'Inmovilizaciones materiales',                       'ASSET',     '2',   true),
(gen_random_uuid(), '210',  'Terrenos y bienes naturales',                       'ASSET',     '21',  true),
(gen_random_uuid(), '211',  'Construcciones',                                    'ASSET',     '21',  true),
(gen_random_uuid(), '212',  'Instalaciones técnicas',                            'ASSET',     '21',  true),
(gen_random_uuid(), '213',  'Maquinaria',                                        'ASSET',     '21',  true),
(gen_random_uuid(), '214',  'Utillaje',                                          'ASSET',     '21',  true),
(gen_random_uuid(), '215',  'Otras instalaciones',                               'ASSET',     '21',  true),
(gen_random_uuid(), '216',  'Mobiliario',                                        'ASSET',     '21',  true),
(gen_random_uuid(), '217',  'Equipos para procesos de información',              'ASSET',     '21',  true),
(gen_random_uuid(), '218',  'Elementos de transporte',                           'ASSET',     '21',  true),
(gen_random_uuid(), '219',  'Otro inmovilizado material',                        'ASSET',     '21',  true),
(gen_random_uuid(), '28',   'Amortización acumulada del inmovilizado',           'ASSET',     '2',   true),
(gen_random_uuid(), '280',  'Amort. acum. inmovilizaciones intangibles',         'ASSET',     '28',  true),
(gen_random_uuid(), '281',  'Amort. acum. inmovilizaciones materiales',          'ASSET',     '28',  true),

-- ============================================================
-- GRUPO 3 — EXISTENCIAS (ASSET)
-- ============================================================
(gen_random_uuid(), '3',    'Existencias',                                       'ASSET',     NULL,  true),
(gen_random_uuid(), '30',   'Comerciales',                                       'ASSET',     '3',   true),
(gen_random_uuid(), '300',  'Mercaderías A',                                     'ASSET',     '30',  true),
(gen_random_uuid(), '31',   'Materias primas',                                   'ASSET',     '3',   true),
(gen_random_uuid(), '310',  'Materias primas A',                                 'ASSET',     '31',  true),
(gen_random_uuid(), '35',   'Productos terminados',                              'ASSET',     '3',   true),
(gen_random_uuid(), '350',  'Productos terminados A',                            'ASSET',     '35',  true),

-- ============================================================
-- GRUPO 4 — ACREEDORES Y DEUDORES COMERCIALES
-- ============================================================
(gen_random_uuid(), '4',    'Acreedores y deudores por operaciones comerciales', 'LIABILITY', NULL,  true),

-- Proveedores y acreedores (LIABILITY)
(gen_random_uuid(), '40',   'Proveedores',                                       'LIABILITY', '4',   true),
(gen_random_uuid(), '400',  'Proveedores (moneda nacional)',                     'LIABILITY', '40',  true),
(gen_random_uuid(), '4000', 'Proveedores',                                       'LIABILITY', '400', true),
(gen_random_uuid(), '4004', 'Proveedores con retención',                         'LIABILITY', '400', true),
(gen_random_uuid(), '401',  'Proveedores, efectos comerciales a pagar',          'LIABILITY', '40',  true),
(gen_random_uuid(), '41',   'Acreedores varios',                                 'LIABILITY', '4',   true),
(gen_random_uuid(), '410',  'Acreedores por prestaciones de servicios',          'LIABILITY', '41',  true),
(gen_random_uuid(), '411',  'Acreedores, efectos comerciales a pagar',           'LIABILITY', '41',  true),

-- Clientes y deudores (ASSET)
(gen_random_uuid(), '43',   'Clientes',                                          'ASSET',     '4',   true),
(gen_random_uuid(), '430',  'Clientes (moneda nacional)',                        'ASSET',     '43',  true),
(gen_random_uuid(), '4300', 'Clientes',                                          'ASSET',     '430', true),
(gen_random_uuid(), '4304', 'Clientes con retención',                            'ASSET',     '430', true),
(gen_random_uuid(), '431',  'Clientes, efectos comerciales a cobrar',            'ASSET',     '43',  true),
(gen_random_uuid(), '44',   'Deudores varios',                                   'ASSET',     '4',   true),
(gen_random_uuid(), '440',  'Deudores',                                          'ASSET',     '44',  true),
(gen_random_uuid(), '441',  'Deudores, efectos comerciales a cobrar',            'ASSET',     '44',  true),

-- Personal
(gen_random_uuid(), '46',   'Personal',                                          'LIABILITY', '4',   true),
(gen_random_uuid(), '460',  'Anticipos de remuneraciones',                       'ASSET',     '46',  true),
(gen_random_uuid(), '465',  'Remuneraciones pendientes de pago',                 'LIABILITY', '46',  true),

-- Administraciones Públicas
(gen_random_uuid(), '47',   'Administraciones Públicas',                         'LIABILITY', '4',   true),
(gen_random_uuid(), '470',  'H.P., deudora por diversos conceptos',              'ASSET',     '47',  true),
(gen_random_uuid(), '4700', 'H.P. deudora por IVA',                              'ASSET',     '470', true),
(gen_random_uuid(), '4709', 'H.P. deudora por devolución de impuestos',          'ASSET',     '470', true),
(gen_random_uuid(), '471',  'Organismos de la Seguridad Social, deudores',       'ASSET',     '47',  true),
(gen_random_uuid(), '472',  'H.P., IVA soportado',                               'ASSET',     '47',  true),
(gen_random_uuid(), '473',  'H.P., retenciones y pagos a cuenta',                'ASSET',     '47',  true),
(gen_random_uuid(), '475',  'H.P., acreedora por conceptos fiscales',            'LIABILITY', '47',  true),
(gen_random_uuid(), '4750', 'H.P., acreedora por IVA',                           'LIABILITY', '475', true),
(gen_random_uuid(), '4751', 'H.P., acreedora por retenciones practicadas',       'LIABILITY', '475', true),
(gen_random_uuid(), '4752', 'H.P., acreedora por impuesto sobre sociedades',     'LIABILITY', '475', true),
(gen_random_uuid(), '476',  'Organismos de la Seguridad Social, acreedores',     'LIABILITY', '47',  true),
(gen_random_uuid(), '477',  'H.P., IVA repercutido',                             'LIABILITY', '47',  true),

-- ============================================================
-- GRUPO 5 — CUENTAS FINANCIERAS
-- ============================================================
(gen_random_uuid(), '5',    'Cuentas financieras',                               'ASSET',     NULL,  true),
(gen_random_uuid(), '52',   'Deudas a corto plazo',                              'LIABILITY', '5',   true),
(gen_random_uuid(), '520',  'Deudas a corto plazo con entidades de crédito',     'LIABILITY', '52',  true),
(gen_random_uuid(), '521',  'Deudas a corto plazo',                              'LIABILITY', '52',  true),
(gen_random_uuid(), '523',  'Proveedores de inmovilizado a corto plazo',         'LIABILITY', '52',  true),
(gen_random_uuid(), '55',   'Otras cuentas no bancarias',                        'LIABILITY', '5',   true),
(gen_random_uuid(), '551',  'Cuenta corriente con socios y administradores',     'ASSET',     '55',  true),
(gen_random_uuid(), '57',   'Tesorería',                                         'ASSET',     '5',   true),
(gen_random_uuid(), '570',  'Caja, moneda nacional',                             'ASSET',     '57',  true),
(gen_random_uuid(), '572',  'Bancos e instituciones de crédito c/c vista',       'ASSET',     '57',  true),
(gen_random_uuid(), '573',  'Bancos e instituciones de crédito, cuentas de ahorro', 'ASSET',  '57',  true),
(gen_random_uuid(), '575',  'Bancos e instituciones de crédito, moneda extranjera', 'ASSET',  '57',  true),

-- ============================================================
-- GRUPO 6 — COMPRAS Y GASTOS (EXPENSE)
-- ============================================================
(gen_random_uuid(), '6',    'Compras y gastos',                                  'EXPENSE',   NULL,  true),
(gen_random_uuid(), '60',   'Compras',                                           'EXPENSE',   '6',   true),
(gen_random_uuid(), '600',  'Compras de mercaderías',                            'EXPENSE',   '60',  true),
(gen_random_uuid(), '601',  'Compras de materias primas',                        'EXPENSE',   '60',  true),
(gen_random_uuid(), '607',  'Trabajos realizados por otras empresas',            'EXPENSE',   '60',  true),
(gen_random_uuid(), '62',   'Servicios exteriores',                              'EXPENSE',   '6',   true),
(gen_random_uuid(), '620',  'Gastos en investigación y desarrollo del ejercicio','EXPENSE',   '62',  true),
(gen_random_uuid(), '621',  'Arrendamientos y cánones',                          'EXPENSE',   '62',  true),
(gen_random_uuid(), '622',  'Reparaciones y conservación',                       'EXPENSE',   '62',  true),
(gen_random_uuid(), '623',  'Servicios de profesionales independientes',         'EXPENSE',   '62',  true),
(gen_random_uuid(), '624',  'Transportes',                                       'EXPENSE',   '62',  true),
(gen_random_uuid(), '625',  'Primas de seguros',                                 'EXPENSE',   '62',  true),
(gen_random_uuid(), '626',  'Servicios bancarios y similares',                   'EXPENSE',   '62',  true),
(gen_random_uuid(), '627',  'Publicidad, propaganda y relaciones públicas',      'EXPENSE',   '62',  true),
(gen_random_uuid(), '628',  'Suministros',                                       'EXPENSE',   '62',  true),
(gen_random_uuid(), '629',  'Otros servicios',                                   'EXPENSE',   '62',  true),
(gen_random_uuid(), '63',   'Tributos',                                          'EXPENSE',   '6',   true),
(gen_random_uuid(), '630',  'Impuesto sobre beneficios',                         'EXPENSE',   '63',  true),
(gen_random_uuid(), '631',  'Otros tributos',                                    'EXPENSE',   '63',  true),
(gen_random_uuid(), '64',   'Gastos de personal',                                'EXPENSE',   '6',   true),
(gen_random_uuid(), '640',  'Sueldos y salarios',                                'EXPENSE',   '64',  true),
(gen_random_uuid(), '641',  'Indemnizaciones',                                   'EXPENSE',   '64',  true),
(gen_random_uuid(), '642',  'Seguridad Social a cargo de la empresa',            'EXPENSE',   '64',  true),
(gen_random_uuid(), '649',  'Otros gastos sociales',                             'EXPENSE',   '64',  true),
(gen_random_uuid(), '65',   'Otros gastos de gestión',                           'EXPENSE',   '6',   true),
(gen_random_uuid(), '650',  'Pérdidas de créditos comerciales incobrables',      'EXPENSE',   '65',  true),
(gen_random_uuid(), '651',  'Resultados de operaciones en común',                'EXPENSE',   '65',  true),
(gen_random_uuid(), '659',  'Otras pérdidas en gestión corriente',               'EXPENSE',   '65',  true),
(gen_random_uuid(), '66',   'Gastos financieros',                                'EXPENSE',   '6',   true),
(gen_random_uuid(), '660',  'Gastos financieros por actualización de provisiones','EXPENSE',  '66',  true),
(gen_random_uuid(), '661',  'Intereses de obligaciones y bonos',                 'EXPENSE',   '66',  true),
(gen_random_uuid(), '662',  'Intereses de deudas',                               'EXPENSE',   '66',  true),
(gen_random_uuid(), '665',  'Descuentos sobre ventas por pronto pago',           'EXPENSE',   '66',  true),
(gen_random_uuid(), '669',  'Otros gastos financieros',                          'EXPENSE',   '66',  true),
(gen_random_uuid(), '68',   'Dotaciones para amortizaciones',                    'EXPENSE',   '6',   true),
(gen_random_uuid(), '680',  'Amortización del inmovilizado intangible',          'EXPENSE',   '68',  true),
(gen_random_uuid(), '681',  'Amortización del inmovilizado material',            'EXPENSE',   '68',  true),
(gen_random_uuid(), '682',  'Amortización de las inversiones inmobiliarias',     'EXPENSE',   '68',  true),

-- ============================================================
-- GRUPO 7 — VENTAS E INGRESOS (REVENUE)
-- ============================================================
(gen_random_uuid(), '7',    'Ventas e ingresos',                                 'REVENUE',   NULL,  true),
(gen_random_uuid(), '70',   'Ventas de mercaderías, producción propia y servicios', 'REVENUE', '7',  true),
(gen_random_uuid(), '700',  'Ventas de mercaderías',                             'REVENUE',   '70',  true),
(gen_random_uuid(), '701',  'Ventas de productos terminados A',                  'REVENUE',   '70',  true),
(gen_random_uuid(), '705',  'Prestaciones de servicios',                         'REVENUE',   '70',  true),
(gen_random_uuid(), '706',  'Descuentos sobre ventas por pronto pago',           'REVENUE',   '70',  true),
(gen_random_uuid(), '708',  'Devoluciones de ventas y operaciones similares',    'REVENUE',   '70',  true),
(gen_random_uuid(), '709',  'Rappels sobre ventas',                              'REVENUE',   '70',  true),
(gen_random_uuid(), '74',   'Subvenciones, donaciones y legados',                'REVENUE',   '7',   true),
(gen_random_uuid(), '740',  'Subvenciones a la explotación',                     'REVENUE',   '74',  true),
(gen_random_uuid(), '75',   'Otros ingresos de gestión',                         'REVENUE',   '7',   true),
(gen_random_uuid(), '751',  'Resultados de operaciones en común',                'REVENUE',   '75',  true),
(gen_random_uuid(), '752',  'Ingresos por arrendamientos',                       'REVENUE',   '75',  true),
(gen_random_uuid(), '753',  'Ingresos de propiedad industrial cedida en explotación', 'REVENUE', '75', true),
(gen_random_uuid(), '759',  'Ingresos por servicios diversos',                   'REVENUE',   '75',  true),
(gen_random_uuid(), '76',   'Ingresos financieros',                              'REVENUE',   '7',   true),
(gen_random_uuid(), '760',  'Ingresos de participaciones en instrumentos de patrimonio', 'REVENUE', '76', true),
(gen_random_uuid(), '761',  'Ingresos de valores representativos de deuda',      'REVENUE',   '76',  true),
(gen_random_uuid(), '762',  'Ingresos de créditos a largo plazo',                'REVENUE',   '76',  true),
(gen_random_uuid(), '763',  'Ingresos de créditos a corto plazo',                'REVENUE',   '76',  true),
(gen_random_uuid(), '765',  'Descuentos sobre compras por pronto pago',          'REVENUE',   '76',  true),
(gen_random_uuid(), '769',  'Otros ingresos financieros',                        'REVENUE',   '76',  true);
