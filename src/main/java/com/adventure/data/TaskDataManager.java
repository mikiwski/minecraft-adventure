package com.adventure.data;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskDataManager {
    private static Map<UUID, PlayerTaskData> playerDataCache = new HashMap<>();

    public static void initialize() {
        // Data manager initialized
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
