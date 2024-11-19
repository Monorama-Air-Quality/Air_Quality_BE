package com.sungjin.airquailitymonitordemo.dto.request;

public record Location(
        Integer latitude,  // Integer로 변경
        Integer longitude  // Integer로 변경
) {
    public boolean isValid() {
        return (latitude != null && longitude != null) || (latitude == null && longitude == null);
    }

    public boolean exists() {
        return latitude != null && longitude != null;
    }

    // 위도의 시작 범위 (예: 37 -> 37.0)
    public Double getLatitudeStart() {
        return latitude != null ? latitude.doubleValue() : null;
    }

    // 위도의 끝 범위 (예: 37 -> 37.999999)
    public Double getLatitudeEnd() {
        return latitude != null ? latitude.doubleValue() + 0.999999 : null;
    }

    // 경도의 시작 범위
    public Double getLongitudeStart() {
        return longitude != null ? longitude.doubleValue() : null;
    }

    // 경도의 끝 범위
    public Double getLongitudeEnd() {
        return longitude != null ? longitude.doubleValue() + 0.999999 : null;
    }
}