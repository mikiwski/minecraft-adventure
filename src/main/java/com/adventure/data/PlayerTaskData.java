package com.adventure.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerTaskData {
    private int currentLevel;
    private Set<Integer> completedTasks;
    private Map<Integer, Integer> taskProgress;

    public PlayerTaskData() {
        this.currentLevel = 1;
        this.completedTasks = new HashSet<>();
        this.taskProgress = new HashMap<>();
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

    public int getTaskProgress(int taskId) {
        return taskProgress.getOrDefault(taskId, 0);
    }

    public void setTaskProgress(int taskId, int progress) {
        taskProgress.put(taskId, progress);
    }

    public void addTaskProgress(int taskId, int amount) {
        int current = getTaskProgress(taskId);
        taskProgress.put(taskId, current + amount);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("currentLevel", currentLevel);
        NbtList completedList = new NbtList();
        for (Integer taskId : completedTasks) {
            NbtCompound taskNbt = new NbtCompound();
            taskNbt.putInt("taskId", taskId);
            completedList.add(taskNbt);
        }
        nbt.put("completedTasks", completedList);
        
        // Save task progress
        NbtList progressList = new NbtList();
        for (Map.Entry<Integer, Integer> entry : taskProgress.entrySet()) {
            NbtCompound progressNbt = new NbtCompound();
            progressNbt.putInt("taskId", entry.getKey());
            progressNbt.putInt("progress", entry.getValue());
            progressList.add(progressNbt);
        }
        nbt.put("taskProgress", progressList);
        return nbt;
    }

    public static PlayerTaskData fromNbt(NbtCompound nbt) {
        PlayerTaskData data = new PlayerTaskData();
        data.currentLevel = nbt.getInt("currentLevel").orElse(1);
        NbtList completedList = nbt.getList("completedTasks").orElse(new NbtList());
        for (int i = 0; i < completedList.size(); i++) {
            NbtElement element = completedList.get(i);
            if (element instanceof NbtCompound taskNbt) {
                taskNbt.getInt("taskId").ifPresent(data.completedTasks::add);
            }
        }
        
        // Load task progress
        NbtList progressList = nbt.getList("taskProgress").orElse(new NbtList());
        for (int i = 0; i < progressList.size(); i++) {
            NbtElement element = progressList.get(i);
            if (element instanceof NbtCompound progressNbt) {
                int taskId = progressNbt.getInt("taskId").orElse(-1);
                int progress = progressNbt.getInt("progress").orElse(0);
                if (taskId > 0) {
                    data.taskProgress.put(taskId, progress);
                }
            }
        }
        return data;
    }

    public static PlayerTaskData get(ServerPlayerEntity player) {
        return TaskDataManager.getPlayerData(player);
    }
}
