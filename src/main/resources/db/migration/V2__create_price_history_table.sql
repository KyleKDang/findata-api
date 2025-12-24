CREATE TABLE price_history (
    id BIGSERIAL PRIMARY KEY,
    ticker VARCHAR(10) NOT NULL,
    date DATE NOT NULL,
    open NUMERIC(12, 4) NOT NULL,
    high NUMERIC(12, 4) NOT NULL,
    low NUMERIC(12, 4) NOT NULL,
    close NUMERIC(12, 4) NOT NULL,
    volume BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ticker_date UNIQUE (ticker, date),
    CONSTRAINT fk_ticker FOREIGN KEY (ticker) REFERENCES stocks(ticker) ON DELETE CASCADE
);

CREATE INDEX idx_price_ticker_date ON price_history(ticker, date DESC);
CREATE INDEX idx_price_date ON price_history(date DESC);

COMMENT ON TABLE price_history IS 'Daily price history for all tracked securities';
COMMENT ON COLUMN price_history.ticker IS 'Stock ticker symbol (foreign key to stocks table)';
COMMENT ON COLUMN price_history.date IS 'Trading date';
COMMENT ON COLUMN price_history.open IS 'Opening price for the trading day';
COMMENT ON COLUMN price_history.high IS 'Highest price during the trading day';
COMMENT ON COLUMN price_history.low IS 'Lowest price during the trading day';
COMMENT ON COLUMN price_history.close IS 'Closing price for the trading day';
COMMENT ON COLUMN price_history.volume IS 'Total trading volume for the day';