package com.adventure.tracking.listeners;

import com.adventure.AdventureMod;
import com.adventure.data.PlayerTaskData;
import com.adventure.network.TaskSyncPacket;
import com.adventure.task.Task;
import com.adventure.task.TaskManager;
import com.adventure.task.TaskType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class MobKillListener {
    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
                checkMobKillForAllTasks(player, entity);
            }
        });
    }

    private static void checkMobKillForAllTasks(ServerPlayerEntity player, LivingEntity entity) {
        List<Task> activeTasks = TaskManager.getInstance().getActiveTasks(player);
        PlayerTaskData data = PlayerTaskData.get(player);
        
        for (Task task : activeTasks) {
            if (task.getType() != TaskType.KILL_MOB) {
                continue;
            }
            
            // Skip if already completed
            if (data.isTaskCompleted(task.getId())) {
                continue;
            }
            
            if (task.matchesTarget(entity)) {
                data.addTaskProgress(task.getId(), 1);
                int currentProgress = data.getTaskProgress(task.getId());
                syncTaskProgress(player, task.getId(), currentProgress, task.getTargetAmount());
                
                AdventureMod.LOGGER.info("Kill task {} progress: {}/{}", 
                    task.getId(), currentProgress, task.getTargetAmount());
                
                if (currentProgress >= task.getTargetAmount()) {
                    completeTaskAndAdvance(player, task, data);
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
        
        var reward = com.adventure.reward.RewardGiver.giveReward(player, task);
        
        com.adventure.network.TaskCompletedPacket.sendToPlayer(player, task.getId(), 
            task.getTranslationKey(), reward);
        
        syncAllActiveTasks(player, data);
        
        AdventureMod.LOGGER.info("Kill task {} completed by player {}", 
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
