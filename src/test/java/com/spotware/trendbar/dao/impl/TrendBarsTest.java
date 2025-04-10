package com.spotware.trendbar.dao.impl;

import com.spotware.trendbar.dao.api.TrendBarDao;
import com.spotware.trendbar.model.PeriodType;
import com.spotware.trendbar.model.Symbol;
import com.spotware.trendbar.model.TrendBar;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class TrendBarsTest {

	@Test
	public void saveSingleTrendBarAndFindIt() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;
		final long timestamp = 1000L;
		final long openPrice = 12345;

		final TrendBar trendBar = new TrendBar(symbol, period, timestamp, openPrice);

		final TrendBarDao trendBarDao = new TrendBars();
		trendBarDao.save(trendBar);

		final List<TrendBar> result = trendBarDao.findByPeriod(symbol, period, timestamp, timestamp);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(openPrice, result.get(0).openPrice());
	}

	@Test
	public void saveSeveralTrendBarsAndFindThemInCorrectOrder() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final long timestampFirst = 1000L;
		final long timestampSecond = 2000L;
		final long timestampThird = 3000L;

		final long openPriceFirst = 1;
		final long openPriceSecond = 2;
		final long openPriceThird = 3;

		final TrendBar first = new TrendBar(symbol, period, timestampFirst, openPriceFirst);
		final TrendBar second = new TrendBar(symbol, period, timestampSecond, openPriceSecond);
		final TrendBar third = new TrendBar(symbol, period, timestampThird, openPriceThird);

		final TrendBarDao trendBarDao = new TrendBars();
		trendBarDao.save(first);
		trendBarDao.save(second);
		trendBarDao.save(third);

		List<TrendBar> result = trendBarDao.findByPeriod(symbol, period, 500L, 3500L);
		assertEquals(3, result.size());
		assertEquals(timestampFirst, result.get(0).timestamp());
		assertEquals(timestampSecond, result.get(1).timestamp());
		assertEquals(timestampThird, result.get(2).timestamp());
	}

	@Test
	public void noSavedTrendBarsTryToFind() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final TrendBarDao trendBarDao = new TrendBars();

		assertThrows(Exception.class, () -> trendBarDao.findByPeriod(symbol, period, 0L, 1L));
	}


	@Test
	public void testSeveralTrendBarsFoundByOneRequest() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final long timestampFirst = 1000L;
		final long timestampSecond = 2000L;

		final long openPriceFirst = 1;
		final long openPriceSecond = 2;

		final TrendBar first = new TrendBar(symbol, period, timestampFirst, openPriceFirst);
		final TrendBar second = new TrendBar(symbol, period, timestampSecond, openPriceSecond);

		final TrendBarDao trendBarDao = new TrendBars();

		trendBarDao.save(first);
		trendBarDao.save(second);

		List<TrendBar> result = trendBarDao.findByPeriod(symbol, period, timestampFirst, timestampFirst);
		assertEquals(1, result.size());

		result = trendBarDao.findByPeriod(symbol, period, timestampSecond, timestampSecond);
		assertEquals(1, result.size());

		result = trendBarDao.findByPeriod(symbol, period, timestampFirst, timestampSecond);
		assertEquals(2, result.size());
	}


	@Test
	public void saveAndRewriteDuplicatedTrendBar() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final long timestamp = 1000L;
		final long openPrice = 1;

		final TrendBar first = new TrendBar(symbol, period, timestamp, openPrice);
		final TrendBar second = new TrendBar(symbol, period, timestamp, openPrice);

		final TrendBarDao trendBarDao = new TrendBars();
		trendBarDao.save(first);
		trendBarDao.save(second);

		final List<TrendBar> result = trendBarDao.findByPeriod(symbol, period, timestamp, timestamp);
		assertEquals(1, result.size());
		assertEquals(openPrice, result.get(0).openPrice());
	}

	@Test
	public void fromAfterToWhenRequestingTrendBarsFromHistoryThrowsException() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final TrendBar trendBar = new TrendBar(symbol, period, 1L, 1);

		final TrendBarDao trendBarDao = new TrendBars();
		trendBarDao.save(trendBar);

		assertThrows(IllegalArgumentException.class, () -> trendBarDao.findByPeriod(symbol, period, 2L, 1L));
	}

	@Test
	public void nullPointerExceptionWhenSymbolIsNullWhenSaving() throws Exception {
		final Symbol symbol = null;
		final PeriodType period = PeriodType.M1;

		final TrendBar trendBar = new TrendBar(symbol, period, 1L, 1);

		final TrendBarDao trendBarDao = new TrendBars();

		assertThrows(Exception.class, () -> trendBarDao.save(trendBar));
	}

	@Test
	public void nullPointerExceptionWhenSymbolIsNullWhenRequesting() throws Exception {
		final Symbol symbol = null;
		final PeriodType period = PeriodType.M1;

		final TrendBarDao trendBarDao = new TrendBars();

		assertThrows(Exception.class, () -> trendBarDao.findByPeriod(symbol, period, 0L, 1L));
	}


	@Test
	public void nullPointerExceptionWhenPeriodTimeIsNullWhenSaving() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = null;

		final TrendBar trendBar = new TrendBar(symbol, period, 1L, 1);

		final TrendBarDao trendBarDao = new TrendBars();

		assertThrows(Exception.class, () -> trendBarDao.save(trendBar));
	}

	@Test
	public void nullPointerExceptionWhenPeriodTimeIsNullWhenRequesting() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = null;

		final TrendBarDao trendBarDao = new TrendBars();

		assertThrows(Exception.class, () -> trendBarDao.findByPeriod(symbol, period, 0L, 1L));
	}

	// going crazy here
	@Test
	public void negativeTimeBarFound() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final long timestamp = -1000L;

		final TrendBar trendBar = new TrendBar(symbol, period, timestamp, 1);

		final TrendBarDao trendBarDao = new TrendBars();
		trendBarDao.save(trendBar);

		final List<TrendBar> result = trendBarDao.findByPeriod(symbol, period, timestamp, timestamp);
		assertEquals(1, result.size());
	}

	@Test
	public void ConcurrentSavingWhileConcurrentReading() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final int readerThreadCount = 5;
		final int writerThreadCount = 10;
		final int trendBarsPerThread = 1000;
		final int expectedTotal = writerThreadCount * trendBarsPerThread;

		final TrendBarDao trendBarDao = new TrendBars();

		final CountDownLatch writersLatch = new CountDownLatch(writerThreadCount);
		final CountDownLatch readersLatch = new CountDownLatch(readerThreadCount);

		// somewhy failed to execute with try-with-resources clause. Not had enough time to fix, closed manually.
		final ExecutorService executor = Executors.newCachedThreadPool();

		for (int i = 0; i < writerThreadCount; i++) {
			final int threadIndex = i;
			executor.submit(() -> {
				for (int j = 0; j < trendBarsPerThread; j++) {
					long timestamp = threadIndex * 100_000L + j;
					TrendBar tb = new TrendBar(symbol, period, timestamp, 1 + j);
					try {
						trendBarDao.save(tb);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				writersLatch.countDown();
			});
		}

		for (int i = 0; i < readerThreadCount; i++) {
			executor.submit(() -> {
				try {
					while (writersLatch.getCount() > 0) {
						List<TrendBar> result = trendBarDao.findByPeriod(symbol, period, 0, Long.MAX_VALUE);
						assertNotNull(result, "Найденный список не должен быть null");
						result.forEach(tb -> assertEquals(symbol.name(), tb.symbol().name()));
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					readersLatch.countDown();
				}
			});
		}

		writersLatch.await();
		readersLatch.await();

		executor.shutdown();

		List<TrendBar> finalResults = trendBarDao.findByPeriod(symbol, period, 0, Long.MAX_VALUE);
		assertEquals(expectedTotal, finalResults.size());
	}


	@Test
	public void parallelSaveAndRequest() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final int operationCount = 1000;

		final ExecutorService executor = Executors.newCachedThreadPool();

		final CountDownLatch latch = new CountDownLatch(operationCount * 2);

		final TrendBarDao trendBarDao = new TrendBars();
		for (int i = 0; i < operationCount; i++) {
			final long timestamp = i;
			executor.submit(() -> {
				TrendBar trendBar = new TrendBar(symbol, period, timestamp, 1 + timestamp);
				try {
					trendBarDao.save(trendBar);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				latch.countDown();
			});

			executor.submit(() -> {
				List<TrendBar> result;
				try {
					result = trendBarDao.findByPeriod(symbol, period, 0, operationCount);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				assertNotNull(result);
				latch.countDown();
			});
		}

		latch.await();

		executor.shutdown();

		List<TrendBar> result = trendBarDao.findByPeriod(symbol, period, 0, operationCount);
		assertFalse(result.isEmpty());

		for (int i = 0; i < result.size() - 1; i++) {
			assertTrue(result.get(i).timestamp() <= result.get(i + 1).timestamp());
		}
	}
}