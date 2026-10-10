package com.speedwalking.model;

public enum InfractionType {
    FLEXAO(">", "Flexão"),
    SUSPENSAO("~", "Suspensão"),
    CONTACTO("~", "Suspensão"); // Backward compatibility with previous records

    private final String symbol;
    private final String displayName;

    InfractionType(String symbol, String displayName) {
        this.symbol = symbol;
        this.displayName = displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static InfractionType fromString(String val) {
        if (val == null) return null;
        String normalized = val.trim().toLowerCase();
        if (normalized.equals("flexao") || normalized.equals("flexão") || normalized.equals(">")) {
            return FLEXAO;
        }
        if (normalized.equals("suspensao") || normalized.equals("suspensão") || normalized.equals("contacto") || normalized.equals("perda de contacto") || normalized.equals("~")) {
            return SUSPENSAO;
        }
        return InfractionType.valueOf(val.toUpperCase());
    }
}
