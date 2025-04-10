package com.spotware.trendbar.model;

public enum PeriodType {
	M1(60000L),
	H1(3600000L),
	D1(86400000L);

	private final long durationMillis;

	PeriodType(long durationMillis) {
		this.durationMillis = durationMillis;
	}

	public long duration() {
		return durationMillis;
	}
}
