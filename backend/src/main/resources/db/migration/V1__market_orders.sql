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

