package com.findata.api.service;

import com.findata.api.model.entity.PriceHistory;
import com.findata.api.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    public PriceHistory savePrice(PriceHistory priceHistory) {
        return priceHistoryRepository.save(priceHistory);
    }

    public List<PriceHistory> savePrices(List<PriceHistory> prices) {
        return priceHistoryRepository.saveAll(prices);
    }

    public List<PriceHistory> getPriceHistory(String ticker) {
        return priceHistoryRepository.findByTickerOrderByDateDesc(ticker);
    }

    public List<PriceHistory> getPriceHistoryInRange(String ticker, LocalDate startDate, LocalDate endDate) {
        return priceHistoryRepository.findByTickerAndDateBetween(ticker, startDate, endDate);
    }

    public Optional<PriceHistory> getPriceByDate(String ticker, LocalDate date) {
        return priceHistoryRepository.findByTickerAndDate(ticker, date);
    }

    public Optional<PriceHistory> getLatestPrice(String ticker) {
        return priceHistoryRepository.findFirstByTickerOrderByDateDesc(ticker);
    }
}
