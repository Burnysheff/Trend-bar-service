package com.spotware.trendbar.model;

import java.util.Objects;

public final class TrendBarMeta {
	private final String symbolName;
	private final PeriodType period;

	public TrendBarMeta(Symbol symbol, PeriodType period) throws Exception {
		if (symbol == null || period == null) {
			throw new Exception("Null parameters for symbol or period");
		}
		this.symbolName = symbol.name();
		this.period = period;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TrendBarMeta compared = (TrendBarMeta) o;
		return symbolName.equals(compared.symbolName) && period.equals(compared.period);
	}

	@Override
	public int hashCode() {
		return Objects.hash(symbolName, period);
	}
}
