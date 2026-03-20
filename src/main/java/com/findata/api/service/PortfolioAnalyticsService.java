package com.findata.api.service;

import com.findata.api.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioAnalyticsService {

    private final PriceHistoryRepository priceHistoryRepository;
}
