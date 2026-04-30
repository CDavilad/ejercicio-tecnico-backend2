-- ==============================================
-- BaseDatos.sql - Banco Microservicios
-- ==============================================

-- ---- SCHEMA MS-CLIENTES ----

CREATE TABLE IF NOT EXISTS persona (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    genero      VARCHAR(20),
    edad        INTEGER,
    identificacion VARCHAR(20) UNIQUE NOT NULL,
    direccion   VARCHAR(200),
    telefono    VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS cliente (
    id          BIGINT PRIMARY KEY REFERENCES persona(id),
    clienteid   VARCHAR(50) UNIQUE NOT NULL,
    contrasena  VARCHAR(255) NOT NULL,
    estado      BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---- SCHEMA MS-CUENTAS ----

CREATE TABLE IF NOT EXISTS cuenta (
    id              BIGSERIAL PRIMARY KEY,
    numero_cuenta   VARCHAR(20) UNIQUE NOT NULL,
    tipo_cuenta     VARCHAR(20) NOT NULL,
    saldo_inicial   NUMERIC(15,2) NOT NULL DEFAULT 0,
    saldo_disponible NUMERIC(15,2) NOT NULL DEFAULT 0,
    estado          BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id      VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS movimiento (
    id              BIGSERIAL PRIMARY KEY,
    fecha           TIMESTAMP NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    valor           NUMERIC(15,2) NOT NULL,
    saldo           NUMERIC(15,2) NOT NULL,
    cuenta_id       BIGINT NOT NULL REFERENCES cuenta(id)
);
