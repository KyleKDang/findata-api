package com.findata.api.service;

import com.findata.api.config.AlphaVantageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlphaVantageService {

    private final AlphaVantageProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchDailyPrices(String ticker) {
        String url = buildURL(ticker);

        log.info("Fetching daily prices for ticker: {}", ticker);

        try {
            String response = restTemplate.getForObject(url, String.class);
            log.error("Successfully fetched data for {}", ticker);
            return response;
        } catch (Exception e) {
            log.error("Error fetching data for {}: {}", ticker, e.getMessage());
            throw new RuntimeException("Failed to fetch data from Alpha Vantage", e);
        }
    }

    private String buildURL(String ticker) {
        return UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", ticker)
                .queryParam("apikey", properties.getApiKey())
                .toUriString();
    }
}
