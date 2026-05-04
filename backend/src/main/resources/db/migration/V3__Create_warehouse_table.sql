DROP TABLE IF EXISTS warehouse CASCADE;

-- Tabla para almacenar bodegas
CREATE TABLE warehouse (
       id BIGSERIAL PRIMARY KEY,
       code VARCHAR(20) NOT NULL UNIQUE,
       name VARCHAR(100) NOT NULL,
       address VARCHAR(200),
       city VARCHAR(100),
       created_at DATE NOT NULL DEFAULT CURRENT_DATE
);