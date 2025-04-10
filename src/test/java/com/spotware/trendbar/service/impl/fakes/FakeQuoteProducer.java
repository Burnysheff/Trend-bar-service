package com.spotware.trendbar.service.impl.fakes;

import com.spotware.trendbar.model.Quote;
import com.spotware.trendbar.model.Symbol;
import com.spotware.trendbar.service.api.QuoteProducer;

import java.time.Instant;
import java.util.Random;

public class FakeQuoteProducer implements QuoteProducer {
	final Random random = new Random();

	@Override
	public Quote nextQuote() throws InterruptedException {
		return nextQuoteDelay(0);
	}

	public Quote nextQuoteDelay(long delay) throws InterruptedException {
		return new Quote(random.nextLong(0, 1000), Symbol.values()[(random.nextInt(Symbol.values().length))], Instant.now().toEpochMilli() + delay);
	}
}
