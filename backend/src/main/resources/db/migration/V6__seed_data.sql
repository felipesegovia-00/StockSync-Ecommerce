-- Seed data
-- Usa ON CONFLICT para ser ejecutado múltiples veces sin duplicar

-- ==========================================
-- CATEGORÍAS
-- ==========================================
INSERT INTO categories (name, description, created_at, updated_at)
VALUES
    ('Electrónica', 'Productos electrónicos, gadgets y accesorios tecnológicos', NOW(), NOW()),
    ('Ropa y Accesorios', 'Prendas de vestir, calzado y accesorios de moda', NOW(), NOW()),
    ('Hogar', 'Artículos para el hogar, decoración y utensilios', NOW(), NOW()),
    ('Deportes', 'Equipamiento deportivo y ropa deportiva', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- ==========================================
-- BODEGAS
-- ==========================================
INSERT INTO warehouses (code, name, address, city, date)
VALUES
    ('STK-CEN', 'Bodega Central', 'Av. Principal 1234', 'Santiago', CURRENT_DATE),
    ('STK-NRT', 'Bodega Norte', 'Calle Los Olivos 567', 'Antofagasta', CURRENT_DATE),
    ('STK-SUR', 'Bodega Sur', 'Ruta 5 Sur Km 890', 'Concepción', CURRENT_DATE)
ON CONFLICT (code) DO NOTHING;

-- ==========================================
-- PRODUCTOS
-- ==========================================
-- Nota: stock se calcula como suma de stocks por bodega
-- Primero insertamos con stock=0, luego se actualiza vía la tabla stocks

-- Electrónica (asumiendo id=1)
INSERT INTO products (name, description, price, stock, sku, image_url, active, category_id, created_at, updated_at)
SELECT 'Auriculares Bluetooth Pro', 'Auriculares inalámbricos con cancelación de ruido activa', 59990, 0, 'AUD-BT-001', '', TRUE, id, NOW(), NOW()
FROM categories WHERE name = 'Electrónica'
AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'AUD-BT-001');

INSERT INTO products (name, description, price, stock, sku, image_url, active, category_id, created_at, updated_at)
SELECT 'Cargador Rápido USB-C 65W', 'Cargador GaN compacto compatible con laptops y smartphones', 24990, 0, 'CHR-USBC-65W', '', TRUE, id, NOW(), NOW()
FROM categories WHERE name = 'Electrónica'
AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CHR-USBC-65W');

INSERT INTO products (name, description, price, stock, sku, image_url, active, category_id, created_at, updated_at)
SELECT 'Teclado Mecánico RGB', 'Teclado mecánico inalámbrico con switches Cherry MX', 89990, 0, 'TEC-MEC-RGB', '', TRUE, id, NOW(), NOW()
FROM categories WHERE name = 'Electrónica'
AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'TEC-MEC-RGB');

-- Ropa y Accesorios (asumiendo id=2)
INSERT INTO products (name, description, price, stock, sku, image_url, active, category_id, created_at, updated_at)
SELECT 'Polera Algodón Premium', 'Polera de algodón orgánico, corte moderno', 19990, 0, 'POL-ALG-001', '', TRUE, id, NOW(), NOW()
FROM categories WHERE name = 'Ropa y Accesorios'
AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'POL-ALG-001');

INSERT INTO products (name, description, price, stock, sku, image_url, active, category_id, created_at, updated_at)
SELECT 'Mochila Urbana 25L', 'Mochila impermeable con compartimiento para laptop', 45990, 0, 'MOC-URB-25L', '', TRUE, id, NOW(), NOW()
FROM categories WHERE name = 'Ropa y Accesorios'
AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'MOC-URB-25L');

-- Hogar (asumiendo id=3)
INSERT INTO products (name, description, price, stock, sku, image_url, active, category_id, created_at, updated_at)
SELECT 'Set Ollas Antiadherentes', 'Set de 5 ollas con recubrimiento cerámico', 79990, 0, 'HOG-OLL-005', '', TRUE, id, NOW(), NOW()
FROM categories WHERE name = 'Hogar'
AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'HOG-OLL-005');

INSERT INTO products (name, description, price, stock, sku, image_url, active, category_id, created_at, updated_at)
SELECT 'Lámpara LED Inteligente', 'Lámpara WiFi compatible con Alexa y Google Home', 34990, 0, 'HOG-LED-WF', '', TRUE, id, NOW(), NOW()
FROM categories WHERE name = 'Hogar'
AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'HOG-LED-WF');

-- Deportes (asumiendo id=4)
INSERT INTO products (name, description, price, stock, sku, image_url, active, category_id, created_at, updated_at)
SELECT 'Botella Térmica Acero 750ml', 'Botella térmica que mantiene la temperatura por 12 horas', 15990, 0, 'DEP-BOT-750', '', TRUE, id, NOW(), NOW()
FROM categories WHERE name = 'Deportes'
AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'DEP-BOT-750');

INSERT INTO products (name, description, price, stock, sku, image_url, active, category_id, created_at, updated_at)
SELECT 'Mat Yoga Eco 6mm', 'Mat de yoga ecológico con superficie antideslizante', 29990, 0, 'DEP-MAT-006', '', TRUE, id, NOW(), NOW()
FROM categories WHERE name = 'Deportes'
AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'DEP-MAT-006');

-- ==========================================
-- STOCK POR BODEGA
-- ==========================================
-- Distribuimos stock en las bodegas y actualizamos el stock global del producto

-- Auriculares: 15 en Central, 8 en Norte
INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 15, NOW()
FROM products p, warehouses w
WHERE p.sku = 'AUD-BT-001' AND w.code = 'STK-CEN'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'AUD-BT-001')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-CEN')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 8, NOW()
FROM products p, warehouses w
WHERE p.sku = 'AUD-BT-001' AND w.code = 'STK-NRT'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'AUD-BT-001')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-NRT')
);

UPDATE products SET stock = 23 WHERE sku = 'AUD-BT-001';

-- Cargador USB-C: 30 en Central, 15 en Sur
INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 30, NOW()
FROM products p, warehouses w
WHERE p.sku = 'CHR-USBC-65W' AND w.code = 'STK-CEN'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'CHR-USBC-65W')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-CEN')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 15, NOW()
FROM products p, warehouses w
WHERE p.sku = 'CHR-USBC-65W' AND w.code = 'STK-SUR'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'CHR-USBC-65W')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-SUR')
);

UPDATE products SET stock = 45 WHERE sku = 'CHR-USBC-65W';

-- Teclado Mecánico: 10 en Central, 5 en Norte, 7 en Sur
INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 10, NOW()
FROM products p, warehouses w
WHERE p.sku = 'TEC-MEC-RGB' AND w.code = 'STK-CEN'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'TEC-MEC-RGB')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-CEN')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 5, NOW()
FROM products p, warehouses w
WHERE p.sku = 'TEC-MEC-RGB' AND w.code = 'STK-NRT'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'TEC-MEC-RGB')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-NRT')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 7, NOW()
FROM products p, warehouses w
WHERE p.sku = 'TEC-MEC-RGB' AND w.code = 'STK-SUR'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'TEC-MEC-RGB')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-SUR')
);

UPDATE products SET stock = 22 WHERE sku = 'TEC-MEC-RGB';

-- Polera Algodón: 50 en Central
INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 50, NOW()
FROM products p, warehouses w
WHERE p.sku = 'POL-ALG-001' AND w.code = 'STK-CEN'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'POL-ALG-001')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-CEN')
);

UPDATE products SET stock = 50 WHERE sku = 'POL-ALG-001';

-- Mochila Urbana: 20 en Central, 12 en Norte
INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 20, NOW()
FROM products p, warehouses w
WHERE p.sku = 'MOC-URB-25L' AND w.code = 'STK-CEN'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'MOC-URB-25L')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-CEN')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 12, NOW()
FROM products p, warehouses w
WHERE p.sku = 'MOC-URB-25L' AND w.code = 'STK-NRT'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'MOC-URB-25L')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-NRT')
);

UPDATE products SET stock = 32 WHERE sku = 'MOC-URB-25L';

-- Set Ollas: 6 en Central, 4 en Sur
INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 6, NOW()
FROM products p, warehouses w
WHERE p.sku = 'HOG-OLL-005' AND w.code = 'STK-CEN'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'HOG-OLL-005')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-CEN')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 4, NOW()
FROM products p, warehouses w
WHERE p.sku = 'HOG-OLL-005' AND w.code = 'STK-SUR'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'HOG-OLL-005')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-SUR')
);

UPDATE products SET stock = 10 WHERE sku = 'HOG-OLL-005';

-- Lámpara LED: 25 en Central, 10 en Norte
INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 25, NOW()
FROM products p, warehouses w
WHERE p.sku = 'HOG-LED-WF' AND w.code = 'STK-CEN'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'HOG-LED-WF')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-CEN')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 10, NOW()
FROM products p, warehouses w
WHERE p.sku = 'HOG-LED-WF' AND w.code = 'STK-NRT'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'HOG-LED-WF')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-NRT')
);

UPDATE products SET stock = 35 WHERE sku = 'HOG-LED-WF';

-- Botella Térmica: 40 en Central, 20 en Norte, 15 en Sur
INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 40, NOW()
FROM products p, warehouses w
WHERE p.sku = 'DEP-BOT-750' AND w.code = 'STK-CEN'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'DEP-BOT-750')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-CEN')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 20, NOW()
FROM products p, warehouses w
WHERE p.sku = 'DEP-BOT-750' AND w.code = 'STK-NRT'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'DEP-BOT-750')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-NRT')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 15, NOW()
FROM products p, warehouses w
WHERE p.sku = 'DEP-BOT-750' AND w.code = 'STK-SUR'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'DEP-BOT-750')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-SUR')
);

UPDATE products SET stock = 75 WHERE sku = 'DEP-BOT-750';

-- Mat Yoga: 30 en Central, 10 en Sur
INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 30, NOW()
FROM products p, warehouses w
WHERE p.sku = 'DEP-MAT-006' AND w.code = 'STK-CEN'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'DEP-MAT-006')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-CEN')
);

INSERT INTO stocks (product_id, warehouse_id, quantity, last_update)
SELECT p.id, w.id, 10, NOW()
FROM products p, warehouses w
WHERE p.sku = 'DEP-MAT-006' AND w.code = 'STK-SUR'
AND NOT EXISTS (
    SELECT 1 FROM stocks s
    WHERE s.product_id = (SELECT id FROM products WHERE sku = 'DEP-MAT-006')
    AND s.warehouse_id = (SELECT id FROM warehouses WHERE code = 'STK-SUR')
);

UPDATE products SET stock = 40 WHERE sku = 'DEP-MAT-006';
