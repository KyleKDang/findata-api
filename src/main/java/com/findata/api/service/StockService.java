package com.findata.api.service;

import com.findata.api.model.entity.Stock;
import com.findata.api.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Optional<Stock> getStockByTicker(String ticker) {
        return stockRepository.findById(ticker);
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public void deleteStock(String ticker) {
        stockRepository.deleteById(ticker);
    }

    public long getStockCount() {
        return stockRepository.count();
    }
}
