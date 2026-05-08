CREATE TABLE warehouses (
                           id BIGSERIAL PRIMARY KEY,
                           code VARCHAR(20) NOT NULL UNIQUE,
                           name VARCHAR(100) NOT NULL,
                           address VARCHAR(200),
                           city VARCHAR(100),
                           date DATE NOT NULL -- Este nombre 'date' debe coincidir con @Column(name = "date")
);