-- Migration: Create derived_analytics table for pre-computed metrics
-- Purpose: Separate raw price data from computed analytics to improve query performance
-- Trade-off: Adds storage overhead but eliminates real-time calculation cost

CREATE TABLE derived_analytics (
    id BIGSERIAL PRIMARY KEY,
    ticker VARCHAR(10) NOT NULL,
    as_of_date DATE NOT NULL,

    -- Price metrics
    current_price NUMERIC(12, 4) NOT NULL,
    previous_close NUMERIC(12, 4),

    -- Returns
    daily_change NUMERIC(12, 4),
    daily_change_percent NUMERIC(8, 4),
    weekly_change NUMERIC (12, 4),
    weekly_change_percent NUMERIC(8, 4),
    monthly_change NUMERIC(12, 4),
    monthly_change_percent NUMERIC(8, 4),

    -- Moving averages
    moving_average_50_day NUMERIC(12, 4),
    moving_average_200_day NUMERIC(12, 4),

    -- Risk metrics
    volatility_30_day NUMERIC(8, 4),
    sharpe_ratio NUMERIC(8, 4),

    -- Range metrics
    week_52_high NUMERIC(12, 4),
    week_52_low NUMERIC(12, 4),

    -- Volume
    average_volume_30_day BIGINT,

    -- Metadata
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_analytics_ticker FOREIGN KEY (ticker) REFERENCES stocks(ticker) ON DELETE CASCADE,
    CONSTRAINT uk_analytics_ticker_date UNIQUE(ticker, as_of_date)
);

CREATE INDEX idx_analytics_ticker ON derived_analytics(ticker);
CREATE INDEX idx_analytics_date ON derived_analytics(as_of_date);
CREATE INDEX idx_analytics_ticker_date ON derived_analytics(ticker, as_of_date DESC);

COMMENT ON TABLE derived_analytics IS 'Pre-computed analytics metrics to avoid expensive real-time calculations';
COMMENT ON COLUMN derived_analytics.calculated_at IS 'Timestamp when metrics were computed (for cache invalidation)';
COMMENT ON CONSTRAINT uk_analytics_ticker_date ON derived_analytics IS 'One analytics record per ticker per day';