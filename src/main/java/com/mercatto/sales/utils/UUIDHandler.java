package com.mercatto.sales.utils;

import java.util.UUID;

public class UUIDHandler {
    private UUIDHandler() {
        throw new IllegalStateException("Utility class");
    }

    public static UUID getUUUD() {
        return UUID.randomUUID();
    }

    public static UUID toUUID(String income) {
        try {
            return UUID.fromString(income);
        } catch (Exception e) {
            ErrorLogger.logError(e);
            return null;
        }

    }
}
