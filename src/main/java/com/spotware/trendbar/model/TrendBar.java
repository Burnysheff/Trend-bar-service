package com.spotware.trendbar.model;

public final class TrendBar {
    private final Symbol symbol;
    private final PeriodType periodType;
    private final long openPrice;
    private final long timestamp;
    private long closePrice;
    private long highPrice;
    private long lowPrice;

    public TrendBar(Symbol symbol, PeriodType periodType, long timestamp, long openPrice) {
        this.symbol = symbol;
        this.periodType = periodType;
        this.timestamp = timestamp;
        this.openPrice = openPrice;
    }

    public void update(long newPrice) {
        this.closePrice = newPrice;
        this.highPrice = Math.max(this.highPrice, newPrice);
        this.lowPrice = Math.min(this.lowPrice, newPrice);
    }

    public Symbol symbol() {
        return this.symbol;
    }

    public PeriodType periodType() {
        return this.periodType;
    }
    public long openPrice() {
        return this.openPrice;
    }

    public long closePrice() {
        return this.closePrice;
    }

    public long timestamp() {
        return this.timestamp;
    }
}
