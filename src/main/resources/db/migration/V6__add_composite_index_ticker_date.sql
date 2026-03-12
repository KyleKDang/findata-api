-- Migration: Add composite index for analytics queries
-- Purpose: Optimize queries that filter by ticker and sort by date

CREATE INDEX IF NOT EXISTS idx_price_history_ticker_date_desc
    ON price_history (ticker, date DESC);

COMMENT ON INDEX idx_price_history_ticker_date_desc IS
'Composite index for queries filtering by ticker and ordering by date descending';