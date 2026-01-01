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
     * Get all currently active tasks for a player (first 3 uncompleted tasks)
     */
    public List<Task> getActiveTasks(ServerPlayerEntity player) {
        PlayerTaskData data = PlayerTaskData.get(player);
        List<Task> activeTasks = new ArrayList<>();
        
        // Find first 3 uncompleted tasks
        for (Task task : allTasks) {
            if (!data.isTaskCompleted(task.getId())) {
                activeTasks.add(task);
                if (activeTasks.size() >= ACTIVE_TASK_COUNT) {
                    break;
                }
            }
        }
        
        return activeTasks;
    }
    
    /**
     * Get IDs of currently active tasks
     */
    public List<Integer> getActiveTaskIds(ServerPlayerEntity player) {
        return getActiveTasks(player).stream().map(Task::getId).toList();
    }

    /**
     * Check if a task is currently active for the player
     */
    public boolean isTaskActive(ServerPlayerEntity player, Task task) {
        List<Task> activeTasks = getActiveTasks(player);
        return activeTasks.contains(task);
    }

    /**
     * Get tasks to display in HUD (3 active + 2 upcoming)
     */
    public List<Task> getPlayerTasks(ServerPlayerEntity player) {
        PlayerTaskData data = PlayerTaskData.get(player);
        List<Task> playerTasks = new ArrayList<>();
        
        // Get first 5 uncompleted tasks (3 active + 2 upcoming)
        for (Task task : allTasks) {
            if (!data.isTaskCompleted(task.getId())) {
                playerTasks.add(task);
                if (playerTasks.size() >= 5) {
                    break;
                }
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
