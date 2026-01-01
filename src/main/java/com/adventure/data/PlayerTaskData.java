package com.adventure.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
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

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("currentLevel", currentLevel);
        NbtList completedList = new NbtList();
        for (Integer taskId : completedTasks) {
            NbtCompound taskNbt = new NbtCompound();
            taskNbt.putInt("taskId", taskId);
            completedList.add(taskNbt);
        }
        nbt.put("completedTasks", completedList);
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
        return data;
    }

    public static PlayerTaskData get(ServerPlayerEntity player) {
        return TaskDataManager.getPlayerData(player);
    }
}
