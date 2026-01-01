package com.adventure.data;

import com.adventure.AdventureMod;
import com.adventure.network.TaskSyncPacket;
import com.adventure.task.Task;
import com.adventure.task.TaskManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskDataManager {
    private static final Identifier DATA_KEY = Identifier.of(AdventureMod.MOD_ID, "task_data");
    private static final Map<UUID, PlayerTaskData> playerDataCache = new HashMap<>();
    private static final Map<UUID, Path> playerDataDirectories = new HashMap<>();

    public static void initialize() {
        // Send initial task sync when player joins
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            
            // Initialize data directory for this server
            Path serverDataDir = server.getRunDirectory().resolve("adventure_data");
            try {
                Files.createDirectories(serverDataDir);
            } catch (IOException e) {
                AdventureMod.LOGGER.error("Failed to create data directory", e);
            }
            
            // Store directory for this player
            playerDataDirectories.put(player.getUuid(), serverDataDir);
            
            // Load player data from file
            loadPlayerData(player, serverDataDir);
            
            // Delay sync slightly to ensure everything is loaded
            server.execute(() -> {
                syncTaskToPlayer(player);
            });
        });
    }

    private static Path getDataDirectory(ServerPlayerEntity player) {
        Path dir = playerDataDirectories.get(player.getUuid());
        if (dir == null) {
            // Fallback: try to get from server (may not work in all contexts)
            AdventureMod.LOGGER.warn("Data directory not initialized for player {}, using fallback", player.getName().getString());
            return null;
        }
        return dir;
    }

    private static Path getPlayerDataFile(ServerPlayerEntity player) {
        Path dataDir = getDataDirectory(player);
        if (dataDir == null) {
            return null;
        }
        return dataDir.resolve(player.getUuid().toString() + ".dat");
    }

    public static void syncTaskToPlayer(ServerPlayerEntity player) {
        PlayerTaskData data = getPlayerData(player);
        java.util.List<Task> activeTasks = TaskManager.getInstance().getActiveTasks(player);
        
        // Sync all active tasks with their progress
        for (Task task : activeTasks) {
            int progress = data.getTaskProgress(task.getId());
            TaskSyncPacket.sendToPlayer(player, data.getCompletedTasks().size(), 
                task.getId(), progress, task.getTargetAmount());
        }
    }

    public static PlayerTaskData getPlayerData(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        
        // Return cached data if available
        if (playerDataCache.containsKey(playerId)) {
            return playerDataCache.get(playerId);
        }
        
        // Load from file or create new
        Path dataDir = getDataDirectory(player);
        if (dataDir == null) {
            // Return new data if directory not initialized
            PlayerTaskData newData = new PlayerTaskData();
            playerDataCache.put(playerId, newData);
            return newData;
        }
        PlayerTaskData data = loadPlayerData(player, dataDir);
        playerDataCache.put(playerId, data);
        return data;
    }

    private static PlayerTaskData loadPlayerData(ServerPlayerEntity player, Path dataDir) {
        Path dataFile = dataDir.resolve(player.getUuid().toString() + ".dat");
        
        if (Files.exists(dataFile)) {
            try {
                NbtCompound nbt = NbtIo.read(dataFile);
                if (nbt != null) {
                    return PlayerTaskData.fromNbt(nbt);
                }
            } catch (IOException e) {
                AdventureMod.LOGGER.error("Failed to load player data for {}", player.getName().getString(), e);
            }
        }
        
        // Return new data if file doesn't exist or failed to load
        return new PlayerTaskData();
    }

    public static void savePlayerData(ServerPlayerEntity player, PlayerTaskData data) {
        UUID playerId = player.getUuid();
        
        // Update cache
        playerDataCache.put(playerId, data);
        
        // Save to file
        Path dataFile = getPlayerDataFile(player);
        if (dataFile == null) {
            AdventureMod.LOGGER.warn("Cannot save data for player {} - data directory not initialized", player.getName().getString());
            return;
        }
        
        try {
            NbtCompound nbt = new NbtCompound();
            data.writeNbt(nbt);
            NbtIo.write(nbt, dataFile);
        } catch (IOException e) {
            AdventureMod.LOGGER.error("Failed to save player data for {}", player.getName().getString(), e);
        }
    }
}
