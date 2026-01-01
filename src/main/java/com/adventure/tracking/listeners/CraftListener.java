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
        
        for (Task task : activeTasks) {
            if (task.getType() != TaskType.CRAFT_ITEM || task.isCompleted()) {
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
                int newProgress = Math.min(matchingItemCount, targetAmount);
                
                if (newProgress > task.getCurrentProgress()) {
                    task.setCurrentProgress(newProgress);
                    syncTaskProgress(player, task);
                    
                    AdventureMod.LOGGER.debug("Craft task {} progress: {}/{}", 
                        task.getId(), newProgress, targetAmount);
                    
                    if (task.isCompleted()) {
                        TaskManager.getInstance().completeTask(player, task);
                        com.adventure.reward.RewardGiver.giveReward(player, task);
                        AdventureMod.LOGGER.info("Craft task {} completed by player {}", 
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
