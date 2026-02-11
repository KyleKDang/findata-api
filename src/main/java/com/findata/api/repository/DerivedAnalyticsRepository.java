package com.findata.api.repository;

import com.findata.api.model.entity.DerivedAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DerivedAnalyticsRepository extends JpaRepository<DerivedAnalytics, Long> {

    Optional<DerivedAnalytics> findByTickerAndAsOfDate(String ticker, LocalDate asOfDate);

    Optional<DerivedAnalytics> findFirstByTickerOrderByAsOfDateDesc(String ticker);

    void deleteByTickerAndAsOfDate(String ticker, LocalDate asOfDate);
}
