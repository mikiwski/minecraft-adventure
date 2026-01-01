package com.adventure.reward;

import com.adventure.task.Task;
import net.minecraft.server.network.ServerPlayerEntity;

public class RewardGiver {
    public static void giveReward(ServerPlayerEntity player, Task task) {
        if (task.isCompleted()) {
            int xp = RewardCalculator.calculateXPReward(task);
            player.addExperience(xp);
            // TODO: Add item rewards
        }
    }
}

