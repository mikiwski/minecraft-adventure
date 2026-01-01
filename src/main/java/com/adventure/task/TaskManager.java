package com.adventure.task;

import com.adventure.data.PlayerTaskData;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static TaskManager instance;
    private final List<Task> allTasks;
    private final TaskDatabase taskDatabase;
    
    // Number of active tasks at once
    public static final int ACTIVE_TASK_COUNT = 3;

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

    /**
     * Get all currently active tasks for a player (3 tasks)
     */
    public List<Task> getActiveTasks(ServerPlayerEntity player) {
        PlayerTaskData data = PlayerTaskData.get(player);
        int baseLevel = data.getCurrentLevel();
        
        List<Task> activeTasks = new ArrayList<>();
        
        // Get 3 active tasks starting from current level
        for (int i = 0; i < ACTIVE_TASK_COUNT; i++) {
            int taskIndex = baseLevel - 1 + i; // 0-based index
            if (taskIndex >= 0 && taskIndex < allTasks.size()) {
                activeTasks.add(allTasks.get(taskIndex));
            }
        }
        
        return activeTasks;
    }

    /**
     * Check if a task is currently active for the player
     */
    public boolean isTaskActive(ServerPlayerEntity player, Task task) {
        PlayerTaskData data = PlayerTaskData.get(player);
        int baseLevel = data.getCurrentLevel();
        int taskId = task.getId();
        
        // Task is active if its ID is within the active range
        return taskId >= baseLevel && taskId < baseLevel + ACTIVE_TASK_COUNT;
    }

    /**
     * Get tasks to display in HUD (completed + active + upcoming)
     */
    public List<Task> getPlayerTasks(ServerPlayerEntity player) {
        PlayerTaskData data = PlayerTaskData.get(player);
        int baseLevel = data.getCurrentLevel();
        
        List<Task> playerTasks = new ArrayList<>();
        
        // Show 5 tasks: the 3 active ones + 2 upcoming
        int startIndex = baseLevel - 1; // 0-based
        for (int i = 0; i < 5; i++) {
            int taskIndex = startIndex + i;
            if (taskIndex >= 0 && taskIndex < allTasks.size()) {
                playerTasks.add(allTasks.get(taskIndex));
            }
        }
        
        return playerTasks;
    }

    /**
     * Get the first active task (for backwards compatibility)
     */
    public Task getActiveTask(ServerPlayerEntity player) {
        List<Task> activeTasks = getActiveTasks(player);
        return activeTasks.isEmpty() ? null : activeTasks.get(0);
    }

    public Task getTaskById(int id) {
        if (id > 0 && id <= allTasks.size()) {
            return allTasks.get(id - 1);
        }
        return null;
    }

    /**
     * Complete a task and shift the active window
     */
    public void completeTask(ServerPlayerEntity player, Task task) {
        PlayerTaskData data = PlayerTaskData.get(player);
        
        if (task.isCompleted()) {
            data.addCompletedTask(task.getId());
            
            // Check if this is the first active task (lowest ID in active range)
            int baseLevel = data.getCurrentLevel();
            if (task.getId() == baseLevel) {
                // Shift the window - increment level
                data.incrementLevel();
            }
        }
    }
    
    /**
     * Get total number of tasks
     */
    public int getTotalTasks() {
        return allTasks.size();
    }
}
