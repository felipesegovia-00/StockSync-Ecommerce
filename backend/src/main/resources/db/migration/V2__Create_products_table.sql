-- Tabla para almacenar productos
CREATE TABLE products (
id SERIAL PRIMARY KEY, -- Identificador único autoincremental
name VARCHAR(200) NOT NULL, -- Nombre del producto (obligatorio)
description TEXT, -- Descripción opcional del producto
price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
-- Precio con 2 decimales (no puede ser negativo)
stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
-- Cantidad disponible en inventario (no negativa, por defecto 0)
sku VARCHAR(50) UNIQUE,
-- Código único del producto (Stock Keeping Unit), opcional pero no duplicable
image_url VARCHAR(500),
-- URL de la imagen del producto (opcional)
active BOOLEAN NOT NULL DEFAULT TRUE,
-- Indica si el producto está activo o disponible
category_id INTEGER NOT NULL,
-- Referencia a la categoría del producto (obligatoria)
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
-- Fecha de creación automática
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
-- Fecha de última actualización (requiere lógica adicional para actualizarse)
CONSTRAINT fk_product_category
  FOREIGN KEY (category_id)
      REFERENCES categories(id)
      ON DELETE RESTRICT
-- Clave foránea: impide eliminar una categoría si tiene productos asociados
);