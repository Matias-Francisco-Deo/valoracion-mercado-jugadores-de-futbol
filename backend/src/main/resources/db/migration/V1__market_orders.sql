CREATE TABLE portfolio (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    credits DECIMAL(19, 2) NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE token_holding (
    id UUID PRIMARY KEY,
    portfolio_id UUID NOT NULL,
    player_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolio(id),
    CONSTRAINT uk_portfolio_player UNIQUE (portfolio_id, player_id)
);

CREATE TABLE audit_log (
    id UUID PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    counterparty_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    token_amount INT NOT NULL,
    price_per_token DECIMAL(19, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
