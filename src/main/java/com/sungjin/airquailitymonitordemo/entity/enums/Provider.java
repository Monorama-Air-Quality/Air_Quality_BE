package com.sungjin.airquailitymonitordemo.entity.enums;

public enum Provider {
    APPLE("애플"),
    GOOGLE("구글"),
    SAMSUNG("삼성");
    
    private final String provider;

    Provider(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }

    public static Provider toProvider(String provider) {
        try {
            return Provider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 Provider입니다: " + provider);
        }
    }
} 
