package com.adventure.tracking.listeners;

import com.adventure.AdventureMod;
import com.adventure.data.PlayerTaskData;
import com.adventure.network.TaskSyncPacket;
import com.adventure.task.Task;
import com.adventure.task.TaskManager;
import com.adventure.task.TaskType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemPickupListener {
    // Track inventory counts per player to detect item gains
    private static final Map<UUID, Map<String, Integer>> playerInventoryCounts = new HashMap<>();

    public static void register() {
        // Listen to block break events (for collecting logs, cobblestone, etc.)
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
                checkBlockBreakForAllTasks(serverPlayer, state);
            }
        });

        // Periodic inventory check every 20 ticks (1 second)
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkInventoryChangesForAllTasks(player);
            }
        });
    }

    private static void checkBlockBreakForAllTasks(ServerPlayerEntity player, BlockState state) {
        List<Task> activeTasks = TaskManager.getInstance().getActiveTasks(player);
        PlayerTaskData data = PlayerTaskData.get(player);
        
        for (Task task : activeTasks) {
            if (task.getType() != TaskType.COLLECT_ITEM) {
                continue;
            }
            
            // Skip if already completed
            if (data.isTaskCompleted(task.getId())) {
                continue;
            }

            // Check if the broken block is related to the task
            String translationKey = task.getTranslationKey();
            
            boolean matches = false;
            if (translationKey.contains("logs") && state.isIn(BlockTags.LOGS)) {
                matches = true;
            } else if (translationKey.contains("cobblestone") && state.getBlock().getTranslationKey().contains("stone")) {
                matches = true;
            } else if (translationKey.contains("coal") && state.getBlock().getTranslationKey().contains("coal_ore")) {
                matches = true;
            } else if (translationKey.contains("iron") && state.getBlock().getTranslationKey().contains("iron_ore")) {
                matches = true;
            }

            if (matches) {
                data.addTaskProgress(task.getId(), 1);
                int currentProgress = data.getTaskProgress(task.getId());
                syncTaskProgress(player, task.getId(), currentProgress, task.getTargetAmount());
                
                AdventureMod.LOGGER.info("Task {} progress: {}/{}", 
                    task.getId(), currentProgress, task.getTargetAmount());
                
                if (currentProgress >= task.getTargetAmount()) {
                    completeTaskAndAdvance(player, task, data);
                }
            }
        }
    }

    private static void checkInventoryChangesForAllTasks(ServerPlayerEntity player) {
        List<Task> activeTasks = TaskManager.getInstance().getActiveTasks(player);
        PlayerTaskData data = PlayerTaskData.get(player);
        
        for (Task task : activeTasks) {
            if (task.getType() != TaskType.COLLECT_ITEM) {
                continue;
            }
            
            // Skip if already completed
            if (data.isTaskCompleted(task.getId())) {
                continue;
            }

            // Count all items in inventory that match the task
            int matchingItemCount = 0;
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (!stack.isEmpty() && task.matchesItem(stack)) {
                    matchingItemCount += stack.getCount();
                }
            }

            // Update task progress based on current inventory count
            if (matchingItemCount > 0) {
                int targetAmount = task.getTargetAmount();
                int currentProgress = data.getTaskProgress(task.getId());
                int newProgress = Math.min(matchingItemCount, targetAmount);
                
                if (newProgress > currentProgress) {
                    data.setTaskProgress(task.getId(), newProgress);
                    syncTaskProgress(player, task.getId(), newProgress, targetAmount);
                    
                    AdventureMod.LOGGER.debug("Task {} inventory count: {}/{}", 
                        task.getId(), newProgress, targetAmount);
                    
                    if (newProgress >= targetAmount) {
                        completeTaskAndAdvance(player, task, data);
                    }
                }
            }
        }
    }
    
    private static void syncTaskProgress(ServerPlayerEntity player, int taskId, int progress, int targetAmount) {
        PlayerTaskData data = PlayerTaskData.get(player);
        TaskSyncPacket.sendToPlayer(player, data.getCurrentLevel(), taskId, progress, targetAmount);
    }
    
    private static void completeTaskAndAdvance(ServerPlayerEntity player, Task task, PlayerTaskData data) {
        data.addCompletedTask(task.getId());
        
        // Get reward
        var reward = com.adventure.reward.RewardGiver.giveReward(player, task);
        
        // Send completion notification to client
        com.adventure.network.TaskCompletedPacket.sendToPlayer(player, task.getId(), 
            task.getTranslationKey(), reward);
        
        // Sync updated active tasks
        syncAllActiveTasks(player, data);
        
        AdventureMod.LOGGER.info("Task {} completed by player {}", 
            task.getId(), player.getName().getString());
    }
    
    private static void syncAllActiveTasks(ServerPlayerEntity player, PlayerTaskData data) {
        List<Task> activeTasks = TaskManager.getInstance().getActiveTasks(player);
        for (Task task : activeTasks) {
            int progress = data.getTaskProgress(task.getId());
            TaskSyncPacket.sendToPlayer(player, data.getCompletedTasks().size(), 
                task.getId(), progress, task.getTargetAmount());
        }
    }
}
