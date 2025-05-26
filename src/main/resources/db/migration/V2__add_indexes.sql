-- V2__add_indexes.sql

-- Índices adicionales para optimizar consultas frecuentes
CREATE INDEX idx_orders_user_id_status ON orders(user_id, status);
CREATE INDEX idx_orders_created_at_status ON orders(created_at, status);
CREATE INDEX idx_payments_order_id_status ON payments(order_id, status);
CREATE INDEX idx_payments_created_at_status ON payments(created_at, status);

-- Índice compuesto para búsquedas por usuario y fecha
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at DESC);

-- Índice para búsquedas de pagos por método y estado
CREATE INDEX idx_payments_method_status ON payments(payment_method, status);
