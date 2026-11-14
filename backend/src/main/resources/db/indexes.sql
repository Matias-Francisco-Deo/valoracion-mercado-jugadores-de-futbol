
-- Optimiza la consulta del historial de cotizaciones por jugador y rango de fechas.
CREATE INDEX idx_registros_cotizaciones_player_fecha
ON registros_cotizaciones (player_id, created_at);

-- Optimiza la recuperación de transacciones por usuario, ordenadas cronológicamente, y las consultas de posiciones filtradas por propietario.
CREATE INDEX idx_transactions_owner_date_id
ON transactions (owner_id, date, id);

-- Optimiza la búsqueda de tokens por propietario y jugador, y la consulta del resumen de tokens agrupado por jugador para un usuario.
CREATE INDEX idx_tokens_owner_player
ON tokens (owner_id, player_id);

-- Optimiza las consultas de órdenes de venta filtradas por token y estado.
CREATE INDEX idx_sell_orders_token_status
    ON sell_orders (token_id, status);