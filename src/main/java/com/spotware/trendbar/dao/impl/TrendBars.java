package com.spotware.trendbar.dao.impl;

import com.spotware.trendbar.dao.api.TrendBarDao;
import com.spotware.trendbar.model.PeriodType;
import com.spotware.trendbar.model.Symbol;
import com.spotware.trendbar.model.TrendBar;
import com.spotware.trendbar.model.TrendBarMeta;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
public class TrendBars implements TrendBarDao {
	private final ConcurrentMap<TrendBarMeta, ConcurrentSkipListMap<Long, TrendBar>> storage = new ConcurrentHashMap<>();

	@Override
	public void save(TrendBar trendbar) throws Exception {
		final TrendBarMeta key = new TrendBarMeta(trendbar.symbol(), trendbar.periodType());
		storage.computeIfAbsent(key, k -> new ConcurrentSkipListMap<>()).put(trendbar.timestamp(), trendbar);
	}

	@Override
	public List<TrendBar> findByPeriod(Symbol symbol, PeriodType periodType, long from, long to) throws Exception {
		final TrendBarMeta key = new TrendBarMeta(symbol, periodType);
		final ConcurrentSkipListMap<Long, TrendBar> trendBarMap = storage.get(key);
		if (trendBarMap == null) {
			throw new Exception("No finished thendbars with such parameters");
		}
		return new ArrayList<>(trendBarMap.subMap(from, true, to, true).values());
	}
}
