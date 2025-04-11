package com.spotware.trendbar.service.impl;

import com.spotware.trendbar.model.PeriodType;
import com.spotware.trendbar.model.Quote;
import com.spotware.trendbar.model.Symbol;
import com.spotware.trendbar.model.TrendBar;
import com.spotware.trendbar.service.api.HistoryService;
import com.spotware.trendbar.service.api.TrendBarsAggregateService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrendBarServiceImplTest {

	@Test
	public void testConsumeFirstQuoteNoSaveCalled() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final long timestamp = 10_000L;
		final long price = 1;

		final HistoryService historyService = Mockito.mock(HistoryService.class);
		final TrendBarsAggregateService trendBarService = new TrendBarServiceImpl(historyService);

		final Quote quote = new Quote(price, symbol, timestamp);
		trendBarService.consume(quote);

		// Some troubles calling TimeUnit.sleep, not enough time to fix, so used just Thread.sleep()
		Thread.sleep(100);

		verify(historyService, never()).save(any());
	}

	@Test
	public void consumeQuotesTrendBarWithinSamePeriod() throws Exception {
		final Symbol symbol = Symbol.EURUSD;

		final long timestampFirst = 1000L;
		final long priceFirst = 1;

		final long timestampSecond = 1500L;
		final long priceSecond = 2;

		final HistoryService historyService = Mockito.mock(HistoryService.class);
		final TrendBarsAggregateService trendBarService = new TrendBarServiceImpl(historyService);

		final Quote quoteFirst = new Quote(priceFirst, symbol, timestampFirst);
		trendBarService.consume(quoteFirst);
		Thread.sleep(100);

		final Quote quoteSecond = new Quote(priceSecond, symbol, timestampSecond);
		trendBarService.consume(quoteSecond);
		Thread.sleep(100);

		verify(historyService, never()).save(any());
	}

	@Test
	public void consumeNewPeriodAndCallSave() throws Exception {
		final Symbol symbol = Symbol.EURUSD;

		final long timestampFirst = 1000L;
		final long priceFirst = 1;

		final long timestampSecond = 70000L;
		final long priceSecond = 2;

		final HistoryService historyService = Mockito.mock(HistoryService.class);
		final TrendBarsAggregateService trendBarService = new TrendBarServiceImpl(historyService);

		final Quote quoteFirst = new Quote(priceFirst, symbol, timestampFirst);
		trendBarService.consume(quoteFirst);
		Thread.sleep(100);


		final Quote quoteSecond = new Quote(priceSecond, symbol, timestampSecond);
		trendBarService.consume(quoteSecond);
		Thread.sleep(100);

		verify(historyService, atLeast(1)).save(any());

		ArgumentCaptor<TrendBar> captor = ArgumentCaptor.forClass(TrendBar.class);
		verify(historyService).save(captor.capture());
		TrendBar savedTrendBar = captor.getValue();
		assertEquals(PeriodType.M1, savedTrendBar.periodType());
	}
}