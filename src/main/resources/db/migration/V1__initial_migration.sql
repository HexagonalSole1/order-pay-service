-- V1__initial_migration.sql

-- Tabla de órdenes
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT NOT NULL,
                        order_number VARCHAR(50) NOT NULL UNIQUE,
                        user_id BIGINT NOT NULL,
                        user_email VARCHAR(255) NOT NULL,
                        total_amount DECIMAL(10,2) NOT NULL,
                        tax_amount DECIMAL(10,2) NULL,
                        shipping_amount DECIMAL(10,2) NULL,
                        status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'RETURNED') NOT NULL DEFAULT 'PENDING',
                        shipping_address TEXT NULL,
                        billing_address TEXT NULL,
                        notes TEXT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        CONSTRAINT pk_orders PRIMARY KEY (id),
                        INDEX idx_orders_user_id (user_id),
                        INDEX idx_orders_status (status),
                        INDEX idx_orders_order_number (order_number),
                        INDEX idx_orders_created_at (created_at)
);

-- Tabla de items de órdenes
CREATE TABLE order_items (
                             id BIGINT AUTO_INCREMENT NOT NULL,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             product_name VARCHAR(255) NOT NULL,
                             product_sku VARCHAR(100) NULL,
                             product_image_url VARCHAR(500) NULL,
                             price DECIMAL(10,2) NOT NULL,
                             quantity INT NOT NULL,
                             subtotal DECIMAL(10,2) NOT NULL,
                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             CONSTRAINT pk_order_items PRIMARY KEY (id),
                             CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             INDEX idx_order_items_order_id (order_id),
                             INDEX idx_order_items_product_id (product_id)
);

-- Tabla de pagos
CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT NOT NULL,
                          order_id BIGINT NOT NULL,
                          payment_reference VARCHAR(100) NOT NULL UNIQUE,
                          amount DECIMAL(10,2) NOT NULL,
                          payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH_ON_DELIVERY', 'DIGITAL_WALLET') NOT NULL,
                          status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
                          transaction_id VARCHAR(255) NULL,
                          gateway_response TEXT NULL,
                          processed_at DATETIME NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          CONSTRAINT pk_payments PRIMARY KEY (id),
                          CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                          INDEX idx_payments_order_id (order_id),
                          INDEX idx_payments_status (status),
                          INDEX idx_payments_payment_reference (payment_reference),
                          INDEX idx_payments_transaction_id (transaction_id)
);