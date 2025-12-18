package com.fintrack.fintrack.util;

public class MaskingUtil {
    private MaskingUtil() {}

    public static String maskAccountNum(String raw) {
        if (raw == null || raw.length() < 4) return "****";
        String last4 = raw.substring(raw.length() - 4);
        return "****" + last4;
    }
}
