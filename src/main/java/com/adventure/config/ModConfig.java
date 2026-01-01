package com.adventure.config;

public class ModConfig {
    private static boolean hudEnabled = true;
    private static int hudPositionX = 0; // 0 = right, 1 = left
    private static int hudPositionY = 0; // 0 = top, 1 = bottom

    public static void initialize() {
        // TODO: Load from config file
    }

    public static boolean isHudEnabled() {
        return hudEnabled;
    }

    public static void setHudEnabled(boolean enabled) {
        hudEnabled = enabled;
    }

    public static int getHudPositionX() {
        return hudPositionX;
    }

    public static int getHudPositionY() {
        return hudPositionY;
    }
}

