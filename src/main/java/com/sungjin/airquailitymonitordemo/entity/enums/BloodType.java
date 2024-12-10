package com.sungjin.airquailitymonitordemo.entity.enums;

public enum BloodType {
    A_PLUS("A+"),
    A_MINUS("A-"),
    B_PLUS("B+"),
    B_MINUS("B-"),
    AB_PLUS("AB+"),
    AB_MINUS("AB-"),
    O_PLUS("O+"),
    O_MINUS("O-");

    private final String bloodType;

    BloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getBloodType() {
        return bloodType;
    }

    public static BloodType toBloodType(String bloodType) {
        try {
            return BloodType.valueOf(bloodType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 BloodType입니다: " + bloodType);
        }
    }
}
