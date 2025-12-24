CREATE TABLE stocks (
    ticker VARCHAR(10) PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    sector VARCHAR(100),
    market_cap BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stocks_sector ON stocks(sector);
CREATE INDEX idx_stocks_market_cap ON stocks(market_cap DESC);

COMMENT ON TABLE stocks IS 'Master table of all tracked securities';
COMMENT ON COLUMN stocks.ticker IS 'Stock ticker symbol (e.g., AAPL, MSFT)';
COMMENT ON COLUMN stocks.company_name IS 'Full legal company name';
COMMENT ON COLUMN stocks.sector IS 'Industry sector classification';
COMMENT ON COLUMN stocks.market_cap IS 'Market capitalization in USD';