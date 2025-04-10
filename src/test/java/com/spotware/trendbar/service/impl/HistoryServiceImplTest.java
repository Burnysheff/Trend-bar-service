package com.spotware.trendbar.service.impl;

import com.spotware.trendbar.dao.api.TrendBarDao;
import com.spotware.trendbar.model.PeriodType;
import com.spotware.trendbar.model.Symbol;
import com.spotware.trendbar.model.TrendBar;
import com.spotware.trendbar.service.api.HistoryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoryServiceImplTest {

	@Test
	public void getForPeriodTest() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final long from = 1000L;
		final long to = 2000L;

		final TrendBar first = new TrendBar(symbol, period, from, 1);
		final TrendBar second = new TrendBar(symbol, period, to, 2);
		final Collection<TrendBar> expected = Arrays.asList(first, second);

		final TrendBarDao trendBarDaoMock = Mockito.mock(TrendBarDao.class);
		when(trendBarDaoMock.findByPeriod(symbol, period, from, to)).thenReturn(expected.stream().toList());

		final HistoryService historyService = new HistoryServiceImpl(trendBarDaoMock);

		final Collection<TrendBar> result = historyService.getForPeriod(symbol, period, from, to);

		assertEquals(expected, result);
		verify(trendBarDaoMock, times(1)).findByPeriod(symbol, period, from, to);
	}

	@Test
	public void getForPeriodWithoutToTest() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final long from = 1000L;
		final Collection<TrendBar> expected = Collections.emptyList();

		final TrendBarDao trendBarDaoMock = Mockito.mock(TrendBarDao.class);
		when(trendBarDaoMock.findByPeriod(eq(symbol), eq(period), eq(from), anyLong())).thenReturn(expected.stream().toList());

		final HistoryService historyService = new HistoryServiceImpl(trendBarDaoMock);
		final Collection<TrendBar> result = historyService.getForPeriod(symbol, period, from);

		assertEquals(expected, result);

		ArgumentCaptor<Long> toCaptor = ArgumentCaptor.forClass(Long.class);
		verify(trendBarDaoMock, times(1)).findByPeriod(eq(symbol), eq(period), eq(from), toCaptor.capture());
		assertTrue(toCaptor.getValue() > from);
	}

	@Test
	public void saveMethodTest() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final TrendBarDao trendBarDaoMock = Mockito.mock(TrendBarDao.class);

		final TrendBar trendBar = new TrendBar(symbol, period, 1L, 1);

		final HistoryService historyService = new HistoryServiceImpl(trendBarDaoMock);
		historyService.save(trendBar);
		verify(trendBarDaoMock, times(1)).save(trendBar);
	}

	@Test
	public void getForPeriodExceptionThrown() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final long from = 1000L;
		final long to = 2000L;

		final String exceptionMessage = "Tralalelo Tralala";
		final TrendBarDao trendBarDaoMock = Mockito.mock(TrendBarDao.class);
		when(trendBarDaoMock.findByPeriod(symbol, period, from, to)).thenThrow(new Exception(exceptionMessage));

		final HistoryService historyService = new HistoryServiceImpl(trendBarDaoMock);
		Exception thrown = assertThrows(Exception.class, () -> historyService.getForPeriod(symbol, period, from, to));
		assertEquals(exceptionMessage, thrown.getMessage());
	}

	@Test
	public void saveExceptionThrown() throws Exception {
		final Symbol symbol = Symbol.EURUSD;
		final PeriodType period = PeriodType.M1;

		final TrendBar trendBar = new TrendBar(symbol, period, 1L, 1);

		final String exceptionMessage = "Bombardiro Crocodilo";
		final TrendBarDao trendBarDaoMock = Mockito.mock(TrendBarDao.class);

		doThrow(new Exception(exceptionMessage)).when(trendBarDaoMock).save(any());

		final HistoryService historyService = new HistoryServiceImpl(trendBarDaoMock);
		Exception thrown = assertThrows(Exception.class, () -> historyService.save(trendBar));
		assertEquals(exceptionMessage, thrown.getMessage());
	}
}