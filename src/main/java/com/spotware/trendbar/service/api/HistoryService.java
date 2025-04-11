package com.spotware.trendbar.service.api;

import java.time.Instant;
import java.util.Collection;

import com.spotware.trendbar.model.PeriodType;
import com.spotware.trendbar.model.Symbol;
import com.spotware.trendbar.model.TrendBar;

public interface HistoryService {

    default Collection<TrendBar> getForPeriod(Symbol symbol, PeriodType periodType, Long from) throws Exception {
        return getForPeriod(symbol, periodType, from, Instant.now().toEpochMilli());
    }

    Collection<TrendBar> getForPeriod(Symbol symbol, PeriodType periodType, Long from, Long to) throws Exception;

    void save(TrendBar trendbar) throws Exception;
}
