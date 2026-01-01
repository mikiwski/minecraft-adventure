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
        
        for (Task task : activeTasks) {
            if (task.getType() != TaskType.KILL_MOB || task.isCompleted()) {
                continue;
            }
            
            if (task.matchesTarget(entity)) {
                task.addProgress(1);
                syncTaskProgress(player, task);
                
                AdventureMod.LOGGER.info("Kill task {} progress: {}/{}", 
                    task.getId(), task.getCurrentProgress(), task.getTargetAmount());
                
                if (task.isCompleted()) {
                    TaskManager.getInstance().completeTask(player, task);
                    com.adventure.reward.RewardGiver.giveReward(player, task);
                    AdventureMod.LOGGER.info("Kill task {} completed by player {}", 
                        task.getId(), player.getName().getString());
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
