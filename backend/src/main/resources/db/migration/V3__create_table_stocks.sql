CREATE TABLE stocks (
                        id BIGSERIAL PRIMARY KEY,
                        product_id BIGINT NOT NULL,
                        warehouse_id BIGINT NOT NULL,
                        quantity INTEGER NOT NULL DEFAULT 0,
                        last_update TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products (id),
                        CONSTRAINT fk_stock_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id)
);