package com.adventure.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskDataManager {
    private static final String DATA_KEY = "adventure_player_data";
    private static Map<UUID, PlayerTaskData> playerDataCache = new HashMap<>();

    public static void initialize() {
        // Data manager initialized
    }

    public static PlayerTaskData getPlayerData(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        
        if (playerDataCache.containsKey(playerId)) {
            return playerDataCache.get(playerId);
        }

        // Load from player data
        NbtCompound playerData = player.getPersistentData();
        if (playerData.contains(DATA_KEY)) {
            NbtCompound taskDataNbt = playerData.getCompound(DATA_KEY);
            PlayerTaskData data = PlayerTaskData.fromNbt(taskDataNbt);
            playerDataCache.put(playerId, data);
            return data;
        }

        // Create new data
        PlayerTaskData data = new PlayerTaskData();
        playerDataCache.put(playerId, data);
        savePlayerData(player, data);
        return data;
    }

    public static void savePlayerData(ServerPlayerEntity player, PlayerTaskData data) {
        NbtCompound playerData = player.getPersistentData();
        NbtCompound taskDataNbt = data.writeNbt(new NbtCompound());
        playerData.put(DATA_KEY, taskDataNbt);
        playerDataCache.put(player.getUuid(), data);
    }

    public static void clearCache(UUID playerId) {
        playerDataCache.remove(playerId);
    }
}

