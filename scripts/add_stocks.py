"""
add_stocks.py

Adds the top 25 most important S&P 500 stocks to the FinData API.

Usage:
    python3 scripts/add_stocks.py              # Runs against localhost
    python3 scripts/add_stocks.py <API_URL>    # Runs against a specific API endpoint

After adding stocks, the script can optionally trigger the price data ingestion process.
"""

import sys
import requests

# Top 25 important S&P 500 stocks by market cap / relevance
STOCKS = [
    ("AAPL", "Apple Inc.", "Technology", 3000),
    ("MSFT", "Microsoft Corp.", "Technology", 2800),
    ("GOOGL", "Alphabet Inc.", "Technology", 1500),
    ("AMZN", "Amazon.com Inc.", "Consumer Cyclical", 1600),
    ("NVDA", "NVIDIA Corp.", "Technology", 1200),
    ("META", "Meta Platforms Inc.", "Technology", 900),
    ("TSLA", "Tesla Inc.", "Automotive", 800),
    ("BRK.B", "Berkshire Hathaway Inc.", "Financial", 700),
    ("V", "Visa Inc.", "Financial", 500),
    ("JPM", "JPMorgan Chase & Co.", "Financial", 450),
    ("UNH", "UnitedHealth Group Inc.", "Healthcare", 500),
    ("JNJ", "Johnson & Johnson", "Healthcare", 380),
    ("WMT", "Walmart Inc.", "Consumer Defensive", 400),
    ("MA", "Mastercard Inc.", "Financial", 380),
    ("PG", "Procter & Gamble Co.", "Consumer Defensive", 360),
    ("HD", "The Home Depot Inc.", "Consumer Cyclical", 340),
    ("CVX", "Chevron Corp.", "Energy", 280),
    ("XOM", "Exxon Mobil Corp.", "Energy", 450),
    ("KO", "The Coca-Cola Co.", "Consumer Defensive", 260),
    ("PEP", "PepsiCo Inc.", "Consumer Defensive", 230),
    ("AVGO", "Broadcom Inc.", "Technology", 600),
    ("LLY", "Eli Lilly and Co.", "Healthcare", 550),
    ("ORCL", "Oracle Corp.", "Technology", 280),
    ("NKE", "Nike Inc.", "Consumer Cyclical", 160),
    ("CSCO", "Cisco Systems Inc.", "Technology", 200),
]

def add_stocks(api_url: str) -> None:
    """Add stocks to the API"""
    print(f"Adding {len(STOCKS)} stocks to {api_url}...")
    print(f"{'='*70}")

    success = 0
    failed = 0

    for ticker, company, sector, mcap_billions in STOCKS:
        market_cap = mcap_billions * 1_000_000_000

        payload = {
            "ticker": ticker,
            "companyName": company,
            "sector": sector,
            "marketCap": market_cap
        }

        try:
            response = requests.post(
                f"{api_url}/api/stocks",
                json=payload,
                headers={"Content-Type": "application/json"},
                timeout=10
            )

            if response.status_code in [200, 201]:
                print(f"✓ {ticker:6s} {company:50s}")
                success += 1
            else:
                print(f"✗ {ticker:6s} {company:50s} (HTTP {response.status_code})")
                failed += 1

        except Exception as e:
            print(f"✗ {ticker:6s} {company:50s} (Error: {str(e)})")
            failed += 1

    print(f"{'='*70}")
    print(f"\nSummary:")
    print(f"  ✓ Added/Updated: {success}")
    print(f"  ✗ Failed: {failed}")
    print(f"  Total: {len(STOCKS)}")

    # Ask about triggering ingestion
    if success > 0:
        print(f"\n{'='*70}")
        trigger = input("Trigger price data ingestion now? (y/n): ").strip().lower()
        if trigger == 'y':
            print("\nTriggering ingestion...")
            try:
                response = requests.post(f"{api_url}/api/ingestion/trigger", timeout=10)
                if response.status_code == 200:
                    print("✓ Ingestion triggered successfully!")
                    print(f"  This will take ~{len(STOCKS) * 13 // 60} minutes due to API rate limits.")
                    print(f"  Check status: curl {api_url}/api/ingestion/status/latest")
                else:
                    print(f"✗ Failed to trigger ingestion (HTTP {response.status_code})")
            except Exception as e:
                print(f"✗ Error triggering ingestion: {e}")


if __name__ == "__main__":
    api_url = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8080"
    api_url = api_url.rstrip('/')
    add_stocks(api_url)
