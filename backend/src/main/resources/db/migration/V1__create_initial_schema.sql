CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            created_at TIMESTAMP,
                            updated_at TIMESTAMP
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(200) NOT NULL,
                          description TEXT,
                          price DECIMAL(19, 2) NOT NULL,
                          stock BIGSERIAL NOT NULL,
                          sku VARCHAR(50) UNIQUE,
                          image_url VARCHAR(500),
                          active BOOLEAN DEFAULT TRUE,
                          category_id BIGINT NOT NULL,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories (id)
);