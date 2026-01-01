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
        
        for (Task task : activeTasks) {
            if (task.getType() != TaskType.COLLECT_ITEM || task.isCompleted()) {
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
                task.addProgress(1);
                syncTaskProgress(player, task);
                
                AdventureMod.LOGGER.info("Task {} progress: {}/{}", 
                    task.getId(), task.getCurrentProgress(), task.getTargetAmount());
                
                if (task.isCompleted()) {
                    TaskManager.getInstance().completeTask(player, task);
                    com.adventure.reward.RewardGiver.giveReward(player, task);
                    AdventureMod.LOGGER.info("Task {} completed by player {}", 
                        task.getId(), player.getName().getString());
                }
            }
        }
    }

    private static void checkInventoryChangesForAllTasks(ServerPlayerEntity player) {
        List<Task> activeTasks = TaskManager.getInstance().getActiveTasks(player);
        
        for (Task task : activeTasks) {
            if (task.getType() != TaskType.COLLECT_ITEM || task.isCompleted()) {
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
                int newProgress = Math.min(matchingItemCount, targetAmount);
                
                if (newProgress > task.getCurrentProgress()) {
                    task.setCurrentProgress(newProgress);
                    syncTaskProgress(player, task);
                    
                    AdventureMod.LOGGER.debug("Task {} inventory count: {}/{}", 
                        task.getId(), newProgress, targetAmount);
                    
                    if (task.isCompleted()) {
                        TaskManager.getInstance().completeTask(player, task);
                        com.adventure.reward.RewardGiver.giveReward(player, task);
                        AdventureMod.LOGGER.info("Task {} completed by player {}", 
                            task.getId(), player.getName().getString());
                    }
                }
            }
        }
    }
    
    private static void syncTaskProgress(ServerPlayerEntity player, Task task) {
        PlayerTaskData data = PlayerTaskData.get(player);
        TaskSyncPacket.sendToPlayer(player, data.getCurrentLevel(), 
            task.getId(), task.getCurrentProgress(), task.getTargetAmount());
    }
}
