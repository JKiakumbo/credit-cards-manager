CREATE TABLE credit_cards (
    id UUID PRIMARY KEY,
    card_number VARCHAR(16) UNIQUE NOT NULL,
    card_holder_name VARCHAR(255) NOT NULL,
    expiration_date VARCHAR(7) NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    credit_limit NUMERIC(19,4) NOT NULL,
    available_credit NUMERIC(19,4) NOT NULL,
    current_balance NUMERIC(19,4) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    card_id UUID NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'GBP',
    merchant VARCHAR(255) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (card_id) REFERENCES credit_cards(id)
);

CREATE INDEX idx_credit_cards_user_id ON credit_cards(user_id);
CREATE INDEX idx_credit_cards_card_number ON credit_cards(card_number);
CREATE INDEX idx_transactions_card_id ON transactions(card_id);
CREATE INDEX idx_transactions_timestamp ON transactions(timestamp);