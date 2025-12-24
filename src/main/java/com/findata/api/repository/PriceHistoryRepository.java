package com.findata.api.repository;

import com.findata.api.model.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByTickerOrderByDateDesc(String ticker);

    List<PriceHistory> findByTickerAndDateBetween(String ticker, LocalDate startDate, LocalDate endDate);

    Optional<PriceHistory> findByTickerAndDate(String ticker, LocalDate date);

    Optional<PriceHistory> findFirstByTickerOrderByDateDesc(String ticker);
}
