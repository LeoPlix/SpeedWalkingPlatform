package com.speedwalking.model;

public enum CardCategory {
    YP("Yellow Paddle", "Advertência"),
    RC("Red Card", "Nota de Desqualificação");

    private final String englishName;
    private final String portugueseName;

    CardCategory(String englishName, String portugueseName) {
        this.englishName = englishName;
        this.portugueseName = portugueseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getPortugueseName() {
        return portugueseName;
    }

    public static CardCategory fromString(String val) {
        if (val == null) return null;
        String normalized = val.trim().toUpperCase();
        return CardCategory.valueOf(normalized);
    }
}
