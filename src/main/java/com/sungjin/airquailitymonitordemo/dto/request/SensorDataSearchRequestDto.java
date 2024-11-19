package com.sungjin.airquailitymonitordemo.dto.request;

public record SensorDataSearchRequestDto(
        Location location,
        DateRange dateRange,

        String apiKey
) {
    public boolean hasValidCriteria() {
        return location.isValid() &&
                dateRange.isValid() &&
                dateRange.isValidRange() &&
                hasAtLeastOneSearchCriteria();
    }

    private boolean hasAtLeastOneSearchCriteria() {
        return location.exists() || dateRange.exists();
    }
}