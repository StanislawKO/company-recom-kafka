CREATE TABLE IF NOT EXISTS company
(
    id      BIGSERIAL PRIMARY KEY,
    company VARCHAR(255),
    message_id VARCHAR(255) NOT NULL DEFAULT 'default_message_id',
    first_capital BIGINT NOT NULL DEFAULT 123
);
