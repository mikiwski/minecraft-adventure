package com.adventure;

import com.adventure.data.TaskDataManager;
import com.adventure.network.TaskCompletedPacket;
import com.adventure.network.TaskSyncPacket;
import com.adventure.task.TaskManager;
import com.adventure.tracking.TaskTracker;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdventureMod implements ModInitializer {
    public static final String MOD_ID = "adventure";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Adventure Tasks Mod");

        // Register network packets
        TaskSyncPacket.register();
        TaskCompletedPacket.register();

        // Initialize managers (this also registers the attachment type)
        TaskDataManager.initialize();
        TaskManager.initialize();
        TaskTracker.initialize();

        LOGGER.info("Adventure Tasks Mod initialized successfully");
    }
}

