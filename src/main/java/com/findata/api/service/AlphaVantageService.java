package com.findata.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findata.api.config.AlphaVantageProperties;
import com.findata.api.model.dto.AlphaVantageResponse;
import com.findata.api.model.entity.PriceHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlphaVantageService {

    private final AlphaVantageProperties properties;
    private final PriceHistoryService priceHistoryService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<PriceHistory> fetchAndSaveDailyPrices(String ticker) {
        String url = buildURL(ticker);

        log.info("Fetching daily prices for ticker: {}", ticker);

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);

            AlphaVantageResponse response = objectMapper.readValue(jsonResponse, AlphaVantageResponse.class);

            List<PriceHistory> prices = convertToPriceHistory(ticker, response);

            List<PriceHistory> savedPrices = priceHistoryService.savePrices(prices);

            log.info("Successfully saved {} price records for {}", savedPrices.size(), ticker);
            return savedPrices;

        } catch (Exception e) {
            log.error("Error fetching data for {}: {}", ticker, e.getMessage());
            throw new RuntimeException("Failed to fetch data from Alpha Vantage", e);
        }
    }

    private List<PriceHistory> convertToPriceHistory(String ticker, AlphaVantageResponse response) {
        List<PriceHistory> priceHistories = new ArrayList<>();

        Map<String, AlphaVantageResponse.DailyData> timeSeries = response.getTimeSeries();

        for (Map.Entry<String, AlphaVantageResponse.DailyData> entry : timeSeries.entrySet()) {
            String dateString = entry.getKey();
            AlphaVantageResponse.DailyData dailyData = entry.getValue();

            PriceHistory priceHistory = PriceHistory.builder()
                    .ticker(ticker)
                    .date(LocalDate.parse(dateString))
                    .open(new BigDecimal(dailyData.getOpen()))
                    .high(new BigDecimal(dailyData.getHigh()))
                    .low(new BigDecimal(dailyData.getLow()))
                    .close(new BigDecimal(dailyData.getClose()))
                    .volume(Long.parseLong(dailyData.getVolume()))
                    .build();

            priceHistories.add(priceHistory);
        }

        return priceHistories;
    }

    private String buildURL(String ticker) {
        return UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", ticker)
                .queryParam("apikey", properties.getApiKey())
                .toUriString();
    }
}
