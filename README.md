# FinData Analytics API

A backend system for market data aggregation, storage, and analysis. Provides REST endpoints for querying historical prices, calculating financial metrics, and performing portfolio-level analytics.

**Purpose:** This is a decision support tool that enables data-driven analysis, not a recommendation system or trading bot.

## Tech Stack

- Java 17
- Spring Boot 3.5.9
- PostgreSQL 15
- Maven
- Docker
- Alpha Vantage API
- Apache Commons Math 3.6.1

## API Endpoints

### Stocks

- `POST /api/stocks` - Create stock
- `GET /api/stocks/{ticker}` - Get stock by ticker
- `GET /api/stocks` - List all stocks
- `DELETE /api/stocks/{ticker}` - Delete stock

### Price History

- `POST /api/prices` - Add price entry
- `GET /api/prices/{ticker}` - Get all prices for stock
- `GET /api/prices/{ticker}/latest` - Get latest price
- `GET /api/prices/{ticker}/range?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` - Get price range

### Analytics

- `GET /api/stocks/{ticker}/analytics` - Calculate real-time analytics
- `GET /api/stocks/{ticker}/analytics/cached` - Retrieve pre-computed analytics (faster)
- `GET /api/stocks/{ticker}/predict` - Get trend estimation via linear regression (requires 60+ days of data)

## Example Usage

### Create a Stock

```bash
curl -X POST http://localhost:8080/api/stocks \
  -H "Content-Type: application/json" \
  -d '{
    "ticker": "AAPL",
    "companyName": "Apple Inc.",
    "sector": "Technology",
    "marketCap": 3000000000000
  }'
```

### Get Stock Analytics (Real-time)

```bash
curl http://localhost:8080/api/stocks/AAPL/analytics
```

**Response:**
```json
{
  "ticker": "AAPL",
  "asOfDate": "2025-01-22",
  "currentPrice": 184.30,
  "previousClose": 182.50,
  "dailyChange": 1.80,
  "dailyChangePercent": 0.99,
  "weeklyChange": 5.20,
  "weeklyChangePercent": 2.91,
  "monthlyChange": 12.40,
  "monthlyChangePercent": 7.21,
  "movingAverage50Day": 178.45,
  "movingAverage200Day": 165.32,
  "volatility30Day": 18.52,
  "sharpeRatio": 0.85,
  "week52High": 195.50,
  "week52Low": 142.30,
  "averageVolume30Day": 55000000
}
```

### Get Cached Analytics (Faster)

```bash
curl http://localhost:8080/api/stocks/AAPL/analytics/cached
```

Returns pre-computed analytics from daily job. Much faster than real-time calculation, but may be up to 24 hours old.

### Get Price Predictions

```bash
curl http://localhost:8080/api/stocks/AAPL/predict
```

**Response:**
```json
{
  "ticker": "AAPL",
  "modelType": "ridge_regression",
  "predictionDate": "2025-01-22",
  "predictions": [
    {
      "date": "2025-01-23",
      "predictedPrice": 185.50,
      "confidenceLower": 181.30,
      "confidenceUpper": 189.70
    },
    {
      "date": "2025-01-24",
      "predictedPrice": 186.20,
      "confidenceLower": 182.00,
      "confidenceUpper": 190.40
    }
  ],
  "metrics": {
    "rmse": 2.10,
    "mae": 1.65,
    "rSquared": 0.78,
    "trainSize": 48,
    "testSize": 12
  }
}
```

**Note:** Predictions are simple linear regression baselines for exploratory analysis, not production-grade forecasts.

## Database Schema

### Stocks Table
```sql
ticker VARCHAR(10) PRIMARY KEY
company_name VARCHAR(255) NOT NULL
sector VARCHAR(100)
market_cap BIGINT
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP
```

### Price History Table (Partitioned)
```sql
id BIGSERIAL
ticker VARCHAR(10) FOREIGN KEY REFERENCES stocks(ticker)
date DATE NOT NULL
open NUMERIC(12,4) NOT NULL
high NUMERIC(12,4) NOT NULL
low NUMERIC(12,4) NOT NULL
close NUMERIC(12,4) NOT NULL
volume BIGINT NOT NULL
created_at TIMESTAMP NOT NULL
PRIMARY KEY (id, date)
UNIQUE (ticker, date)
PARTITION BY RANGE (date)
```

**Partitions:** 12 monthly partitions for 2025 (Jan-Dec) plus default partition

### Derived Analytics Table
```sql
id BIGSERIAL PRIMARY KEY
ticker VARCHAR(10) FOREIGN KEY REFERENCES stocks(ticker)
as_of_date DATE NOT NULL
current_price NUMERIC(12,4) NOT NULL
-- ... all analytics metrics ...
calculated_at TIMESTAMP NOT NULL
UNIQUE (ticker, as_of_date)
```

**Purpose:** Stores pre-computed analytics to improve query performance. Separates raw price data from derived metrics.

## Project Structure

```
src/main/java/com/findata/api/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── exception/       # Global exception handlers
├── model/
│   ├── dto/        # Data Transfer Objects
│   └── entity/     # JPA entities
├── repository/      # Spring Data repositories
└── service/         # Business logic
```

## Features

### Automated Data Pipeline
- Scheduled cron job runs daily at 6 PM EST
- Fetches latest stock prices from Alpha Vantage API
- Computes and caches analytics metrics
- Rate-limited to 5 calls per minute (13-second delays)
- Idempotent upsert logic prevents duplicate entries

### Performance Optimizations
- **Table Partitioning**: RANGE partitioning by date delivers 10-50x faster queries
- **Derived Analytics Caching**: Pre-computed metrics eliminate expensive real-time calculations
- **HashSet Filtering**: O(1) duplicate detection vs O(n) with List
- **Bulk Operations**: Batch inserts for 100+ records at once
- **Connection Pooling**: HikariCP with optimized pool settings

### Financial Analytics
- **Price Changes**: Daily, weekly, and monthly returns (absolute and percentage)
- **Moving Averages**: 50-day and 200-day trend indicators
- **Volatility**: 30-day standard deviation of returns
- **Sharpe Ratio**: Risk-adjusted return metric
- **Range Metrics**: 52-week high/low prices
- **Volume Analysis**: 30-day average trading volume

### Machine Learning Price Prediction
- **Algorithm**: Linear regression with regularization (OLS)
- **Features**: 8 engineered features (lag prices, moving averages, volatility)
- **Validation**: 80/20 train/test split with RMSE, MAE, R² metrics
- **Output**: 5-day trend estimates with confidence intervals
- **Requirements**: Minimum 60 days of historical data
- **Note**: Simple baseline for exploratory analysis, not production forecasts

### Data Integrity
- Input validation with custom error messages
- Foreign key constraints with cascade delete
- Unique constraints on (ticker, date) pairs
- BigDecimal precision for financial calculations

## License

MIT