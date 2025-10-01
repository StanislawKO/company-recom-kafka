CREATE TABLE IF NOT EXISTS recommendation
(
    id             BIGSERIAL PRIMARY KEY,
    full_name      VARCHAR(255) NOT NULL,
    company_id     BIGINT       NOT NULL,
    position_id    BIGINT       NOT NULL,
    recommendation VARCHAR(255)
);
