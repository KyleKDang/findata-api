# FinData API

A financial data aggregation API built with Spring Boot and PostgreSQL. Provides REST endpoints for stock data management, historical price tracking, and real-time analytics.

## Tech Stack

- Java 17
- Spring Boot 3.5.9
- PostgreSQL 15
- Maven
- Docker
- Alpha Vantage API

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

- `GET /api/stocks/{ticker}/analytics` - Get financial analytics

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

### Get Stock Analytics

```bash
curl http://localhost:8080/api/stocks/AAPL/analytics
```

**Response:**
```json
{
  "ticker": "AAPL",
  "asOfDate": "2025-12-26",
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
  "week52High": 195.50,
  "week52Low": 142.30,
  "averageVolume30Day": 55000000
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
- Rate-limited to 5 calls per minute (13-second delays)
- Idempotent upsert logic prevents duplicate entries

### Performance Optimizations
- **Table Partitioning**: RANGE partitioning by date delivers 10-50x faster queries
- **HashSet Filtering**: O(1) duplicate detection vs O(n) with List
- **Bulk Operations**: Batch inserts for 100+ records at once
- **Connection Pooling**: HikariCP with optimized pool settings

### Financial Analytics
- **Price Changes**: Daily, weekly, and monthly returns (absolute and percentage)
- **Moving Averages**: 50-day and 200-day trend indicators
- **Volatility**: 30-day standard deviation of returns
- **Range Metrics**: 52-week high/low prices
- **Volume Analysis**: 30-day average trading volume

### Data Integrity
- Input validation with custom error messages
- Foreign key constraints with cascade delete
- Unique constraints on (ticker, date) pairs
- BigDecimal precision for financial calculations

## License

MIT