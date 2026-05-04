-- Tabla para almacenar categorías
CREATE TABLE categories (
id SERIAL PRIMARY KEY, -- Identificador único autoincremental
name VARCHAR(100) NOT NULL UNIQUE, -- Nombre de la categoría (obligatorio y único)
description TEXT, -- Descripción opcional de la categoría
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Fecha de creación automática
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- Fecha de última actualización (requiere lógica extra para mantenerse al día)
);