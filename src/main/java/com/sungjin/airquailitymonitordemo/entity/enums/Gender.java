package com.sungjin.airquailitymonitordemo.entity.enums;

public enum Gender {
    MALE("남자"),
    FEMALE("여자"),
    UNKNOWN("알 수 없음");

    private final String gender;

    Gender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }
    public static Gender toGender(String gender) {
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Gender.UNKNOWN;
        }
    }
}
