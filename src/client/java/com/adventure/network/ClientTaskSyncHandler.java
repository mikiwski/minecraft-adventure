package com.adventure.network;

import com.adventure.AdventureMod;
import com.adventure.data.ClientTaskCache;
import com.adventure.data.ClientTaskDatabase;
import com.adventure.ui.TaskCompletedOverlay;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientTaskSyncHandler {

    public static void register() {
        // Register handler for task sync
        ClientPlayNetworking.registerGlobalReceiver(TaskSyncPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientTaskCache cache = ClientTaskCache.getInstance();
                cache.setCurrentLevel(payload.currentLevel());
                
                var task = ClientTaskDatabase.getInstance().getTask(payload.taskId());
                if (task != null) {
                    task.setCurrentProgress(payload.progress());
                }
                
                AdventureMod.LOGGER.debug("Received task sync: level={}, task={}, progress={}/{}", 
                    payload.currentLevel(), payload.taskId(), payload.progress(), payload.targetAmount());
            });
        });
        
        // Register handler for task completed notification
        ClientPlayNetworking.registerGlobalReceiver(TaskCompletedPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                // Mark task as completed in client database
                ClientTaskCache.getInstance().markTaskCompleted(payload.taskId());
                
                // Show completion overlay with items
                TaskCompletedOverlay.getInstance().show(
                    payload.translationKey(), payload.xpReward(), payload.itemsData());
                
                AdventureMod.LOGGER.info("Task {} completed! XP: {}, Items: {}", 
                    payload.taskId(), payload.xpReward(), payload.itemsData());
            });
        });
    }
}
