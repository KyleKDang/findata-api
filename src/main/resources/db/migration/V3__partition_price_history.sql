ALTER TABLE price_history RENAME TO price_history_old;

ALTER TABLE price_history_old DROP CONSTRAINT IF EXISTS uk_ticker_date;
ALTER TABLE price_history_old DROP CONSTRAINT IF EXISTS fk_ticker;
DROP INDEX IF EXISTS idx_price_ticker_date;
DROP INDEX IF EXISTS idx_price_date;

CREATE TABLE price_history (
    id BIGSERIAL,
    ticker VARCHAR(10) NOT NULL,
    date DATE NOT NULL,
    open NUMERIC(12, 4) NOT NULL,
    high NUMERIC(12, 4) NOT NULL,
    low NUMERIC(12, 4) NOT NULL,
    close NUMERIC(12, 4) NOT NULL,
    volume BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, date),
    CONSTRAINT fk_ticker FOREIGN KEY (ticker) REFERENCES stocks(ticker) ON DELETE CASCADE,
    CONSTRAINT uk_ticker_date UNIQUE (ticker, date)
) PARTITION BY RANGE (date);

CREATE INDEX idx_price_ticker_date ON price_history(ticker, date DESC);
CREATE INDEX idx_price_date ON price_history(date DESC);

CREATE TABLE price_history_2025_01 PARTITION OF price_history
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

CREATE TABLE price_history_2025_02 PARTITION OF price_history
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');

CREATE TABLE price_history_2025_03 PARTITION OF price_history
    FOR VALUES FROM ('2025-03-01') TO ('2025-04-01');

CREATE TABLE price_history_2025_04 PARTITION OF price_history
    FOR VALUES FROM ('2025-04-01') TO ('2025-05-01');

CREATE TABLE price_history_2025_05 PARTITION OF price_history
    FOR VALUES FROM ('2025-05-01') TO ('2025-06-01');

CREATE TABLE price_history_2025_06 PARTITION OF price_history
    FOR VALUES FROM ('2025-06-01') TO ('2025-07-01');

CREATE TABLE price_history_2025_07 PARTITION OF price_history
    FOR VALUES FROM ('2025-07-01') TO ('2025-08-01');

CREATE TABLE price_history_2025_08 PARTITION OF price_history
    FOR VALUES FROM ('2025-08-01') TO ('2025-09-01');

CREATE TABLE price_history_2025_09 PARTITION OF price_history
    FOR VALUES FROM ('2025-09-01') TO ('2025-10-01');

CREATE TABLE price_history_2025_10 PARTITION OF price_history
    FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');

CREATE TABLE price_history_2025_11 PARTITION OF price_history
    FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');

CREATE TABLE price_history_2025_12 PARTITION OF price_history
    FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');

CREATE TABLE price_history_default PARTITION OF price_history DEFAULT;

INSERT INTO price_history (id, ticker, date, open, high, low, close, volume, created_at)
SELECT id, ticker, date, open, high, low, close, volume, created_at
FROM price_history_old;

SELECT setval('price_history_id_seq', (SELECT MAX(id) FROM price_history));

DROP TABLE price_history_old;

COMMENT ON TABLE price_history IS 'Partitioned table storing daily OHLCV price data';
COMMENT ON COLUMN price_history.open IS 'Opening price for the trading day';
COMMENT ON COLUMN price_history.high IS 'Highest price during the trading day';
COMMENT ON COLUMN price_history.low IS 'Lowest price during the trading day';
COMMENT ON COLUMN price_history.close IS 'Closing price for the trading day';
COMMENT ON COLUMN price_history.volume IS 'Trading volume (number of shares traded)';