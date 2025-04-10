package com.spotware.trendbar.service.impl;

import com.spotware.trendbar.dao.api.TrendBarDao;
import com.spotware.trendbar.dao.impl.TrendBars;
import com.spotware.trendbar.model.PeriodType;
import com.spotware.trendbar.model.Quote;
import com.spotware.trendbar.model.Symbol;
import com.spotware.trendbar.service.api.HistoryService;
import com.spotware.trendbar.service.impl.fakes.FakeQuoteProducer;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrendBarServiceImplITCase {

	@Test
	// load parameters: 20 threads, each produces 2_000_000 quotes
	public void HighLoadProcessing() throws Exception {
		final TrendBarDao trendBarDao = new TrendBars();
		final HistoryService historyService = new HistoryServiceImpl(trendBarDao);
		final TrendBarServiceImpl trendBarService = new TrendBarServiceImpl(historyService);

		FakeQuoteProducer quoteProducer = new FakeQuoteProducer();

		final int numberOfThreads = 20;
		final int quotesPerThread = 2_000_000;

		final int quoteDelay = 55;

		ExecutorService generatorExecutor = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		for (int i = 0; i < numberOfThreads; i++) {
			generatorExecutor.submit(() -> {
				for (int j = 0; j < quotesPerThread; j++) {
					Quote quote;
					try {
						quote = quoteProducer.nextQuoteDelay(j * quoteDelay);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					try {
						trendBarService.consume(quote);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				latch.countDown();
			});
		}

		latch.await();
		generatorExecutor.shutdown();

		final Instant now = Instant.now();

		// delta of 1, because quotes producer is based on current system time, and with high load it sometimes acts weird
		assertEquals(quotesPerThread * quoteDelay / 1000 / 60, historyService.getForPeriod(Symbol.EURUSD, PeriodType.M1, 0L, now.toEpochMilli() + quoteDelay * quotesPerThread).size(), 1L);
		assertEquals(quotesPerThread * quoteDelay / 1000 / 60 / 60, historyService.getForPeriod(Symbol.EURUSD, PeriodType.H1, 0L, now.toEpochMilli() + quoteDelay * quotesPerThread).size(), 1L);
		assertEquals(quotesPerThread * quoteDelay / 1000 / 60 / 60 / 24, historyService.getForPeriod(Symbol.EURUSD, PeriodType.D1, 0L, now.toEpochMilli() + quoteDelay * quotesPerThread).size());
	}
}