package com.spotware.trendbar.service.impl;

import com.spotware.trendbar.dao.api.TrendBarDao;
import com.spotware.trendbar.dao.impl.TrendBars;
import com.spotware.trendbar.model.PeriodType;
import com.spotware.trendbar.model.Symbol;
import com.spotware.trendbar.model.TrendBar;
import com.spotware.trendbar.service.api.HistoryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;

@Service
public final class HistoryServiceImpl implements HistoryService {
	private final TrendBarDao trendBars;

	public HistoryServiceImpl(TrendBarDao trendBarDao) {
		this.trendBars = trendBarDao;
	}

	@Override
	public Collection<TrendBar> getForPeriod(Symbol symbol, PeriodType periodType, Long from) throws Exception {
		return this.getForPeriod(symbol, periodType, from, System.currentTimeMillis());
	}

	@Override
	public Collection<TrendBar> getForPeriod(Symbol symbol, PeriodType periodType, Long from, Long to) throws Exception {
		return trendBars.findByPeriod(symbol, periodType, from, to);
	}

	@Override
	public void save(TrendBar trendbar) throws Exception {
		trendBars.save(trendbar);
	}
}
