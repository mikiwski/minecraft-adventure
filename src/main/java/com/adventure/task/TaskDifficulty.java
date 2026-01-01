package com.adventure.task;

public class TaskDifficulty {
    public static int calculateTargetAmount(int baseAmount, int difficulty) {
        // Formuła: baseAmount * (1 + difficulty * 0.5)
        return (int) (baseAmount * (1 + difficulty * 0.5));
    }

    public static int calculateXPReward(int difficulty) {
        // XP: difficulty * 10 (minimalnie)
        return Math.max(10, difficulty * 10);
    }

    public static int calculateItemRewardMultiplier(int difficulty) {
        // Mnożnik nagród przedmiotowych
        return Math.max(1, difficulty / 5 + 1);
    }
}

