CREATE TABLE stocks_movements (
                                 id BIGSERIAL PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 source_warehouse_id BIGINT, -- Puede ser null si es un ingreso inicial
                                 destination_warehouse_id BIGINT, -- Puede ser null si es una salida/merma
                                 quantity INTEGER NOT NULL,
                                 movement_type VARCHAR(50) NOT NULL, -- 'TRANSFER', 'ADD', 'ADJUSTMENT'
                                 created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_movement_product FOREIGN KEY (product_id) REFERENCES products (id),
                                 CONSTRAINT fk_movement_source FOREIGN KEY (source_warehouse_id) REFERENCES warehouses (id),
                                 CONSTRAINT fk_movement_dest FOREIGN KEY (destination_warehouse_id) REFERENCES warehouses (id)
);