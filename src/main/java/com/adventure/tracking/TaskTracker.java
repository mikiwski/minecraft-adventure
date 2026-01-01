package com.adventure.tracking;

import com.adventure.tracking.listeners.CraftListener;
import com.adventure.tracking.listeners.ItemPickupListener;
import com.adventure.tracking.listeners.MobKillListener;

public class TaskTracker {
    public static void initialize() {
        ItemPickupListener.register();
        CraftListener.register();
        MobKillListener.register();
    }
}

