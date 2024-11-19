package com.sungjin.airquailitymonitordemo.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record DateRange(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate
) {
    public boolean isValid() {
        return (startDate != null && endDate != null) || (startDate == null && endDate == null);
    }

    public boolean exists() {
        return startDate != null && endDate != null;
    }

    public boolean isValidRange() {
        return !exists() || !startDate.isAfter(endDate);
    }
}