package com.adventure.tracking.listeners;

import com.adventure.AdventureMod;
import com.adventure.task.Task;
import com.adventure.task.TaskManager;
import com.adventure.task.TaskType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class MobKillListener {
    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (damageSource.getAttacker() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) damageSource.getAttacker();
                checkMobKill(player, entity);
            }
        });
    }

    private static void checkMobKill(ServerPlayerEntity player, LivingEntity entity) {
        Task activeTask = TaskManager.getInstance().getActiveTask(player);
        if (activeTask != null && activeTask.getType() == TaskType.KILL_MOB) {
            if (activeTask.matchesTarget(entity)) {
                activeTask.addProgress(1);
                
                if (activeTask.isCompleted()) {
                    TaskManager.getInstance().completeTask(player, activeTask);
                    com.adventure.reward.RewardGiver.giveReward(player, activeTask);
                    AdventureMod.LOGGER.info("Task {} completed by player {}", activeTask.getId(), player.getName().getString());
                }
            }
        }
    }
}

