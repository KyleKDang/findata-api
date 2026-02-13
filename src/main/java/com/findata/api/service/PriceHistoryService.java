package com.findata.api.service;

import com.findata.api.model.entity.PriceHistory;
import com.findata.api.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    @Transactional
    public PriceHistory savePrice(PriceHistory priceHistory) {
        return priceHistoryRepository.save(priceHistory);
    }

    @Transactional
    public List<PriceHistory> savePrices(List<PriceHistory> prices) {
        return priceHistoryRepository.saveAll(prices);
    }

    public List<PriceHistory> getPriceHistory(String ticker) {
        return priceHistoryRepository.findByTickerOrderByDateDesc(ticker);
    }

    public Page<PriceHistory> getPriceHistory(String ticker, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return priceHistoryRepository.findByTicker(ticker, pageable);
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
