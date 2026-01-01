package com.adventure.reward;

import com.adventure.task.Task;

public class RewardCalculator {
    public static int calculateXPReward(Task task) {
        return com.adventure.task.TaskDifficulty.calculateXPReward(task.getDifficulty());
    }

    public static int calculateItemRewardMultiplier(Task task) {
        return com.adventure.task.TaskDifficulty.calculateItemRewardMultiplier(task.getDifficulty());
    }
}

