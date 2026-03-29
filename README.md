# FinData Analytics API

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Available-success)](http://54.237.200.251:8080/actuator/health)
[![Deployment](https://img.shields.io/badge/AWS-EC2-orange)]()
[![Database](https://img.shields.io/badge/AWS-RDS%20PostgreSQL-blue)]()

**Live API:** `http://54.237.200.251:8080`

**Quick Test:**
```bash
# Health check
curl http://54.237.200.251:8080/actuator/health

# Get all stocks
curl http://54.237.200.251:8080/api/stocks

# Get analytics for AAPL
curl http://54.237.200.251:8080/api/stocks/AAPL/analytics
```

---

A backend system for market data aggregation, storage, and analysis. Provides REST endpoints for querying historical prices, calculating financial metrics, and performing portfolio-level analytics.

**Purpose:** A decision support tool for data-driven stock analysis.

## Tech Stack

- Java 17
- Spring Boot 3.5.9
- PostgreSQL 15
- Maven
- Docker
- Alpha Vantage API
- Apache Commons Math 3.6.1

## Deployment

- **Compute:** AWS EC2 (t2.micro)
- **Database:** AWS RDS PostgreSQL 15
- **Container Registry:** AWS ECR
- **Containerization:** Docker

## API Endpoints

### Stocks

- `GET /api/stocks/{ticker}` - Get stock by ticker
- `GET /api/stocks` - List all stocks

### Price History

- `GET /api/prices/{ticker}?page=0&size=50` - Get prices with pagination
- `GET /api/prices/{ticker}/latest` - Get latest price
- `GET /api/prices/{ticker}/range?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&page=0&size=50&sortBy=date&sortDirection=desc` - Get price range with pagination and sorting

### Analytics

- `GET /api/stocks/{ticker}/analytics` - Calculate real-time analytics
- `GET /api/stocks/{ticker}/analytics/cached` - Retrieve pre-computed analytics (faster)

### Trend Prediction

- `GET /api/stocks/{ticker}/predict` - Get trend estimation via linear regression (requires 60+ days of data)

### Portfolio Analytics

- `POST /api/portfolio/metrics` - Calculate portfolio-level metrics for user-defined allocations

### Monitoring

- `GET /api/ingestion/status/latest` - Get most recent ingestion job status
- `GET /api/ingestion/status/history` - Get last 10 ingestion job runs
- `GET /api/ingestion/status/failed` - Get all failed ingestion jobs

## Example Usage

### Get Paginated Price History

```bash
# First page (50 results)
curl "http://54.237.200.251:8080/api/prices/AAPL?page=0&size=50"

# Second page
curl "http://54.237.200.251:8080/api/prices/AAPL?page=1&size=50"
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "ticker": "AAPL",
      "date": "2025-01-22",
      "open": 183.50,
      "high": 186.00,
      "low": 182.80,
      "close": 185.50,
      "volume": 52000000
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 50
  },
  "totalPages": 5,
  "totalElements": 250,
  "last": false,
  "first": true
}
```

### Get Price Range with Sorting

```bash
# Sort by closing price, ascending
curl "http://54.237.200.251:8080/api/prices/AAPL/range?startDate=2024-01-01&endDate=2024-12-31&sortBy=close&sortDirection=asc&page=0&size=100"

# Sort by date, descending (default)
curl "http://54.237.200.251:8080/api/prices/AAPL/range?startDate=2024-01-01&endDate=2024-12-31"
```

**Sortable fields:** date, open, high, low, close, volume

### Get Stock Analytics (Real-time)

```bash
curl http://54.237.200.251:8080/api/stocks/AAPL/analytics
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
curl http://54.237.200.251:8080/api/stocks/AAPL/analytics/cached
```

Returns pre-computed analytics from daily job. Much faster than real-time calculation, but may be up to 24 hours old.

### Get Price Predictions

```bash
curl http://54.237.200.251:8080/api/stocks/AAPL/predict
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

### Calculate Portfolio Metrics

```bash
curl -X POST http://54.237.200.251:8080/api/portfolio/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "positions": [
      {"ticker": "AAPL", "weight": 0.40},
      {"ticker": "GOOGL", "weight": 0.35},
      {"ticker": "MSFT", "weight": 0.25}
    ],
    "startDate": "2025-01-01",
    "endDate": "2025-12-31"
  }'

```

**Response:**
```json
{
  "positions": [
    {"ticker": "AAPL", "weight": 0.40},
    {"ticker": "GOOGL", "weight": 0.35},
    {"ticker": "MSFT", "weight": 0.25}
  ],
  "startDate": "2025-01-01",
  "endDate": "2025-12-31",
  "returnPercent": -3.95,
  "volatility": 2.07,
  "sharpeRatio": -1.91,
  "positionMetrics": [
    {
      "ticker": "AAPL",
      "weight": 0.40,
      "returnPercent": -4.50,
      "volatility": 0.94,
      "contribution": -1.80
    },
    {
      "ticker": "GOOGL",
      "weight": 0.35,
      "returnPercent": -10.70,
      "volatility": 3.24,
      "contribution": -3.75
    },
    {
      "ticker": "MSFT",
      "weight": 0.25,
      "returnPercent": 6.38,
      "volatility": 2.23,
      "contribution": 1.60
    }
  ]
}
```

**Note:** This is what-if analysis for a given allocation, not portfolio optimization or recommendation.

### Monitor Ingestion Job Status

```bash
# Get latest job run status
curl http://54.237.200.251:8080/api/ingestion/status/latest

# Get job history (last 10 runs)
curl http://54.237.200.251:8080/api/ingestion/status/history

# Get all failed jobs
curl http://54.237.200.251:8080/api/ingestion/status/failed
```

**Response (latest):**
```json
{
  "id": 42,
  "jobStartedAt": "2025-02-10T18:00:00",
  "jobCompletedAt": "2025-02-10T18:05:30",
  "status": "COMPLETED",
  "totalStocks": 5,
  "stocksSucceeded": 5,
  "stocksFailed": 0,
  "errorMessage": null
}
```

## Error Handling

All errors follow a consistent JSON structure:

### Validation Error (400)

**Request:**
```bash
curl -X POST http://54.237.200.251:8080/api/stocks \
  -H "Content-Type: application/json" \
  -d '{
    "ticker": "",
    "companyName": "Apple Inc."
  }'
```

**Response:**
```json
{
  "timestamp": "2025-01-22T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/api/stocks",
  "fieldErrors": [
    {
      "field": "ticker",
      "rejectedValue": "",
      "message": "Ticker must not be blank"
    }
  ]
}
```

### Business Logic Error (400)

**Request:**
```bash
curl http://54.237.200.251:8080/api/stocks/AAPL/predict
```

**Response (if insufficient data):**
```json
{
  "timestamp": "2025-01-22T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Need at least 60 days of data for prediction",
  "path": "/api/stocks/AAPL/predict"
}
```

### Not Found (404)

**Request:**
```bash
curl http://54.237.200.251:8080/api/stocks/INVALID
```

**Response:**
```json
{
  "timestamp": "2025-01-22T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Stock not found",
  "path": "/api/stocks/INVALID"
}
```

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

### Ingestion Status Table
```sql
id BIGSERIAL PRIMARY KEY
job_started_at TIMESTAMP NOT NULL
job_completed_at TIMESTAMP
status VARCHAR(20) NOT NULL  -- RUNNING, COMPLETED, FAILED
total_stocks INTEGER
stocks_succeeded INTEGER
stocks_failed INTEGER
error_message TEXT
created_at TIMESTAMP NOT NULL
```

**Purpose:** Tracks scheduled data ingestion job runs for monitoring and debugging.

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
- Job execution tracking with status monitoring

### Performance Optimizations
- **Table Partitioning**: RANGE partitioning by date delivers 10-50x faster queries
- **Derived Analytics Caching**: Pre-computed metrics eliminate expensive real-time calculations
- **Composite Indexes**: Optimized for common query patterns (ticker + date)
- **Query Performance Logging**: Automatic logging of slow queries (>100ms)
- **Pagination**: Prevents memory issues when querying large datasets
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

### Portfolio Analytics
- **What-If Analysis**: Calculate metrics for user-defined portfolio allocations
- **Portfolio Return**: Weighted sum of individual stock returns
- **Portfolio Volatility**: Risk measurement for the entire portfolio
- **Portfolio Sharpe Ratio**: Risk-adjusted return for the portfolio
- **Position Metrics**: Individual stock contributions to portfolio performance
- **Flexible Time Periods**: Analyze any date range with historical data

### API Design
- **Pagination**: All list endpoints support page and size parameters
- **Filtering & Sorting**: Flexible query parameters for data exploration
- **Consistent Error Format**: Standardized JSON error responses with field-level details
- **Input Validation**: Request validation with detailed error messages

### Monitoring & Observability
- **Job Status Tracking**: Monitor scheduled ingestion job execution
- **Query Performance Logging**: Automatic detection of slow queries
- **Health Checks**: Spring Boot Actuator endpoints
- **Error Tracking**: Detailed error messages and stack traces for failures

### Data Integrity
- Input validation with custom error messages
- Foreign key constraints with cascade delete
- Unique constraints on (ticker, date) pairs
- BigDecimal precision for financial calculations

## License

MIT