package com.spotware.trendbar.service.impl;

import com.spotware.trendbar.model.PeriodType;
import com.spotware.trendbar.model.Quote;
import com.spotware.trendbar.model.TrendBar;
import com.spotware.trendbar.model.TrendBarMeta;
import com.spotware.trendbar.service.api.HistoryService;
import com.spotware.trendbar.service.api.TrendBarsAggregateService;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public final class TrendBarServiceImpl implements TrendBarsAggregateService {
	private final HistoryService historyService;

	private final BlockingQueue<Quote> quoteQueue = new LinkedBlockingQueue<>();
	private final ConcurrentMap<TrendBarMeta, TrendBar> activeTrendBars = new ConcurrentHashMap<>();
	private final ExecutorService executor;

	public TrendBarServiceImpl(HistoryService historyService) {
		this.historyService = historyService;
		this.executor = Executors.newSingleThreadExecutor();
		this.executor.submit(new QuoteProcessor());
	}

	@Override
	public void consume(Quote quote) throws InterruptedException {
		quoteQueue.offer(quote);
	}

	private class QuoteProcessor implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					Quote quote = quoteQueue.take();
					processQuote(quote);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (Exception ignored) {
			}
		}
	}

	private void processQuote(Quote quote) throws Exception {
		for (final PeriodType period : PeriodType.values()) {
			final long periodDuration = period.duration();
			final long periodStart = (quote.timestamp() / periodDuration) * periodDuration;

			final TrendBarMeta key = new TrendBarMeta(quote.symbol(), period);

			boolean trendBarExistent = activeTrendBars.containsKey(key);
			TrendBar trendBar = null;
			if (trendBarExistent) {
				trendBar = activeTrendBars.get(key);
			}

			if (!trendBarExistent || periodStart > trendBar.timestamp()) {
				if (trendBarExistent) {
					historyService.save(trendBar);
				}
				final TrendBar newTB = new TrendBar(quote.symbol(), period, periodStart, quote.price());
				activeTrendBars.put(key, newTB);
			} else {
				trendBar.update(quote.price());
			}
		}
	}
}
