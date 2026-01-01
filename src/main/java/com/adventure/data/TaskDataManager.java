package com.adventure.data;

import com.adventure.network.TaskSyncPacket;
import com.adventure.task.Task;
import com.adventure.task.TaskManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskDataManager {
    private static Map<UUID, PlayerTaskData> playerDataCache = new HashMap<>();

    public static void initialize() {
        // Send initial task sync when player joins
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            
            // Delay sync slightly to ensure everything is loaded
            server.execute(() -> {
                syncTaskToPlayer(player);
            });
        });
    }

    public static void syncTaskToPlayer(ServerPlayerEntity player) {
        PlayerTaskData data = getPlayerData(player);
        java.util.List<Task> activeTasks = TaskManager.getInstance().getActiveTasks(player);
        
        // Sync all active tasks
        for (Task task : activeTasks) {
            TaskSyncPacket.sendToPlayer(player, data.getCurrentLevel(), 
                task.getId(), task.getCurrentProgress(), task.getTargetAmount());
        }
    }

    public static PlayerTaskData getPlayerData(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        
        if (playerDataCache.containsKey(playerId)) {
            return playerDataCache.get(playerId);
        }

        // Create new data
        PlayerTaskData data = new PlayerTaskData();
        playerDataCache.put(playerId, data);
        return data;
    }

    public static void savePlayerData(ServerPlayerEntity player, PlayerTaskData data) {
        playerDataCache.put(player.getUuid(), data);
    }

    public static void clearCache(UUID playerId) {
        playerDataCache.remove(playerId);
    }
}
