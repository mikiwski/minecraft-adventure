package com.adventure.data;

import com.adventure.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientTaskCache {
    private static final ClientTaskCache instance = new ClientTaskCache();
    private final List<Task> playerTasks;
    private final Map<Integer, Integer> taskProgress;
    private int currentLevel;

    private ClientTaskCache() {
        this.playerTasks = new ArrayList<>();
        this.taskProgress = new HashMap<>();
        this.currentLevel = 1;
    }

    public static ClientTaskCache getInstance() {
        return instance;
    }

    public List<Task> getPlayerTasks() {
        return new ArrayList<>(playerTasks);
    }

    public void setPlayerTasks(List<Task> tasks) {
        this.playerTasks.clear();
        this.playerTasks.addAll(tasks);
    }

    public void setTaskProgress(int taskId, int progress) {
        taskProgress.put(taskId, progress);
        // Update progress in task objects
        for (Task task : playerTasks) {
            if (task.getId() == taskId) {
                task.setCurrentProgress(progress);
                break;
            }
        }
    }

    public int getTaskProgress(int taskId) {
        return taskProgress.getOrDefault(taskId, 0);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    public void clear() {
        playerTasks.clear();
        taskProgress.clear();
        currentLevel = 1;
    }
}

