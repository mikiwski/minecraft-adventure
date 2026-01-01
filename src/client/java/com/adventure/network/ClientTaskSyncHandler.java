package com.adventure.network;

import com.adventure.AdventureMod;
import com.adventure.data.ClientTaskCache;
import com.adventure.data.ClientTaskDatabase;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientTaskSyncHandler {

    public static void register() {
        // Register handler using the shared TaskSyncPacket from main
        ClientPlayNetworking.registerGlobalReceiver(TaskSyncPacket.ID, (payload, context) -> {
            // Update client cache on main thread
            context.client().execute(() -> {
                ClientTaskCache cache = ClientTaskCache.getInstance();
                cache.setCurrentLevel(payload.currentLevel());
                
                // Update progress in the task database
                var task = ClientTaskDatabase.getInstance().getTask(payload.taskId());
                if (task != null) {
                    task.setCurrentProgress(payload.progress());
                }
                
                AdventureMod.LOGGER.info("Received task sync: level={}, task={}, progress={}/{}", 
                    payload.currentLevel(), payload.taskId(), payload.progress(), payload.targetAmount());
            });
        });
    }
}
