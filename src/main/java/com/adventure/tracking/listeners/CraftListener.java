package com.adventure.tracking.listeners;

import com.adventure.AdventureMod;
import com.adventure.data.PlayerTaskData;
import com.adventure.network.TaskSyncPacket;
import com.adventure.task.Task;
import com.adventure.task.TaskManager;
import com.adventure.task.TaskType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class CraftListener {

    public static void register() {
        // Check inventory periodically for crafted items
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkCraftedItemsForAllTasks(player);
            }
        });
    }

    private static void checkCraftedItemsForAllTasks(ServerPlayerEntity player) {
        List<Task> activeTasks = TaskManager.getInstance().getActiveTasks(player);
        PlayerTaskData data = PlayerTaskData.get(player);
        
        for (Task task : activeTasks) {
            if (task.getType() != TaskType.CRAFT_ITEM) {
                continue;
            }
            
            // Skip if already completed
            if (data.isTaskCompleted(task.getId())) {
                continue;
            }

            // Count matching crafted items in inventory
            int matchingItemCount = 0;
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (!stack.isEmpty() && task.matchesItem(stack)) {
                    matchingItemCount += stack.getCount();
                }
            }

            if (matchingItemCount > 0) {
                int targetAmount = task.getTargetAmount();
                int currentProgress = data.getTaskProgress(task.getId());
                int newProgress = Math.min(matchingItemCount, targetAmount);
                
                if (newProgress > currentProgress) {
                    data.setTaskProgress(task.getId(), newProgress);
                    com.adventure.data.TaskDataManager.savePlayerData(player, data);
                    syncTaskProgress(player, task.getId(), newProgress, targetAmount);
                    
                    AdventureMod.LOGGER.debug("Craft task {} progress: {}/{}", 
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
        com.adventure.data.TaskDataManager.savePlayerData(player, data);
        
        var reward = com.adventure.reward.RewardGiver.giveReward(player, task);
        
        com.adventure.network.TaskCompletedPacket.sendToPlayer(player, task.getId(), 
            task.getTranslationKey(), reward);
        
        syncAllActiveTasks(player, data);
        
        AdventureMod.LOGGER.info("Craft task {} completed by player {}", 
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
