package com.adventure.data;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

public class PlayerTaskData {
    private int currentLevel;
    private Set<Integer> completedTasks;

    public PlayerTaskData() {
        this.currentLevel = 1;
        this.completedTasks = new HashSet<>();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    public void incrementLevel() {
        this.currentLevel++;
    }

    public Set<Integer> getCompletedTasks() {
        return completedTasks;
    }

    public void addCompletedTask(int taskId) {
        completedTasks.add(taskId);
    }

    public boolean isTaskCompleted(int taskId) {
        return completedTasks.contains(taskId);
    }

    public static PlayerTaskData get(ServerPlayerEntity player) {
        // TODO: Implement proper data storage
        return new PlayerTaskData();
    }
}

