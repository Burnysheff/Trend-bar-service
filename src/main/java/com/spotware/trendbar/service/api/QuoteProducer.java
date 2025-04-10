package com.spotware.trendbar.service.api;

import com.spotware.trendbar.model.Quote;

public interface QuoteProducer {

    Quote nextQuote() throws InterruptedException;
}
