package com.spotware.trendbar.service.api;

import com.spotware.trendbar.model.Quote;

public interface TrendBarsAggregateService {
    void consume(Quote quote) throws InterruptedException;
}

