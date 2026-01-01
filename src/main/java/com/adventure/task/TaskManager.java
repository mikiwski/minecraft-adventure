package com.adventure.task;

import com.adventure.data.PlayerTaskData;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static TaskManager instance;
    private final List<Task> allTasks;
    private final TaskDatabase taskDatabase;

    private TaskManager() {
        this.taskDatabase = new TaskDatabase();
        this.allTasks = new ArrayList<>();
        initializeTasks();
    }

    public static void initialize() {
        if (instance == null) {
            instance = new TaskManager();
        }
    }

    public static TaskManager getInstance() {
        return instance;
    }

    private void initializeTasks() {
        // Tasks will be loaded from TaskDatabase
        allTasks.addAll(taskDatabase.getAllTasks());
    }

    public List<Task> getPlayerTasks(ServerPlayerEntity player) {
        PlayerTaskData data = PlayerTaskData.get(player);
        int currentLevel = data.getCurrentLevel();
        
        // Get 5 tasks: 2 previous, 1 active, 2 next
        List<Task> playerTasks = new ArrayList<>();
        
        // Get active task (current level)
        if (currentLevel > 0 && currentLevel <= allTasks.size()) {
            // Add 2 previous tasks
            for (int i = Math.max(0, currentLevel - 3); i < currentLevel - 1; i++) {
                if (i >= 0 && i < allTasks.size()) {
                    playerTasks.add(allTasks.get(i));
                }
            }
            
            // Add active task
            playerTasks.add(allTasks.get(currentLevel - 1));
            
            // Add 2 next tasks
            for (int i = currentLevel; i < Math.min(currentLevel + 2, allTasks.size()); i++) {
                playerTasks.add(allTasks.get(i));
            }
        } else if (currentLevel == 0) {
            // First time - show first 3 tasks
            for (int i = 0; i < Math.min(3, allTasks.size()); i++) {
                playerTasks.add(allTasks.get(i));
            }
        }
        
        // Fill with empty tasks if needed
        while (playerTasks.size() < 5 && allTasks.size() > playerTasks.size()) {
            int nextIndex = playerTasks.size();
            if (nextIndex < allTasks.size()) {
                playerTasks.add(allTasks.get(nextIndex));
            }
        }
        
        return playerTasks;
    }

    public Task getActiveTask(ServerPlayerEntity player) {
        PlayerTaskData data = PlayerTaskData.get(player);
        int currentLevel = data.getCurrentLevel();
        
        if (currentLevel > 0 && currentLevel <= allTasks.size()) {
            return allTasks.get(currentLevel - 1);
        }
        return null;
    }

    public Task getTaskById(int id) {
        if (id > 0 && id <= allTasks.size()) {
            return allTasks.get(id - 1);
        }
        return null;
    }

    public void completeTask(ServerPlayerEntity player, Task task) {
        PlayerTaskData data = PlayerTaskData.get(player);
        if (task.isCompleted()) {
            data.incrementLevel();
            data.addCompletedTask(task.getId());
        }
    }
}

