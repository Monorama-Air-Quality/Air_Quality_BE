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
        switch (bloodType) {
            case "A+" -> {return A_PLUS; }
            case "A-" -> {return A_MINUS; }
            case "B+" -> {return B_PLUS; }
            case "B-" -> {return B_MINUS; }
            case "AB+" -> {return AB_PLUS; }
            case "AB-" -> {return AB_MINUS; }
            case "O+" -> {return O_PLUS; }
            case "O-" -> {return O_MINUS; }            
            default -> {
                throw new IllegalArgumentException("지원하지 않는 BloodType입니다: " + bloodType);
            }
        }
    }
}
