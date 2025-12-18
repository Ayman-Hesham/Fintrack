package com.fintrack.fintrack.model;

public enum CategoryColor {
    RED("#FF6B6B"),
    TEAL("#4ECDC4"),
    BLUE("#45B7D1"),
    GREEN("#96CEB4"),
    YELLOW("#FECA57"),
    PINK("#FF9FF3"),
    ROYAL_BLUE("#54A0FF"),
    PURPLE("#5F27CD"),
    CYAN("#00D2D3"),
    ORANGE("#FF9F43"),
    VIOLET("#A55EEA"),
    EMERALD("#26DE81"),
    ROSE("#FD79A8"),
    AMBER("#FDCB6E"),
    INDIGO("#6C5CE7");

    private final String hexCode;

    CategoryColor(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getHexCode() {
        return hexCode;
    }

    public static CategoryColor fromHexCode(String hexCode) {
        for (CategoryColor color : CategoryColor.values()) {
            if (color.getHexCode().equalsIgnoreCase(hexCode)) {
                return color;
            }
        }
        throw new IllegalArgumentException("No enum constant with hex code " + hexCode);
    }
}
