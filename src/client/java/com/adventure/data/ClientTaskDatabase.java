package com.adventure.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Simplified client-side task database for display purposes
 */
public class ClientTaskDatabase {
    private static final ClientTaskDatabase instance = new ClientTaskDatabase();
    private final List<ClientTask> allTasks;
    
    // Number of active tasks at once (must match server)
    public static final int ACTIVE_TASK_COUNT = 3;

    private ClientTaskDatabase() {
        this.allTasks = new ArrayList<>();
        initializeTasks();
    }

    public static ClientTaskDatabase getInstance() {
        return instance;
    }

    private void initializeTasks() {
        // Poziomy 1-20 (Podstawowe)
        allTasks.add(new ClientTask(1, "task.adventure.collect_logs", 10));
        allTasks.add(new ClientTask(2, "task.adventure.collect_planks", 20));
        allTasks.add(new ClientTask(3, "task.adventure.craft_sticks", 16));
        allTasks.add(new ClientTask(4, "task.adventure.craft_torches", 32));
        allTasks.add(new ClientTask(5, "task.adventure.collect_cobblestone", 20));
        allTasks.add(new ClientTask(6, "task.adventure.craft_furnace", 1));
        allTasks.add(new ClientTask(7, "task.adventure.collect_coal", 10));
        allTasks.add(new ClientTask(8, "task.adventure.craft_chest", 1));
        allTasks.add(new ClientTask(9, "task.adventure.collect_iron", 15));
        allTasks.add(new ClientTask(10, "task.adventure.craft_iron_pickaxe", 1));
        allTasks.add(new ClientTask(11, "task.adventure.kill_zombies", 5));
        allTasks.add(new ClientTask(12, "task.adventure.kill_skeletons", 3));
        allTasks.add(new ClientTask(13, "task.adventure.collect_wheat", 20));
        allTasks.add(new ClientTask(14, "task.adventure.collect_carrots", 10));
        allTasks.add(new ClientTask(15, "task.adventure.collect_potatoes", 10));
        allTasks.add(new ClientTask(16, "task.adventure.kill_cows", 3));
        allTasks.add(new ClientTask(17, "task.adventure.kill_pigs", 3));
        allTasks.add(new ClientTask(18, "task.adventure.kill_chickens", 5));
        allTasks.add(new ClientTask(19, "task.adventure.craft_wooden_sword", 1));
        allTasks.add(new ClientTask(20, "task.adventure.craft_stone_axe", 1));
    }

    /**
     * Get tasks to display: first 5 uncompleted tasks (3 active + 2 upcoming)
     */
    public List<ClientTask> getUncompletedTasks() {
        List<ClientTask> result = new ArrayList<>();
        
        for (ClientTask task : allTasks) {
            if (!ClientTaskCache.getInstance().isTaskCompleted(task.getId())) {
                result.add(task);
                if (result.size() >= 5) {
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Get tasks to display (legacy method for compatibility)
     */
    public List<ClientTask> getTasksForLevel(int currentLevel) {
        return getUncompletedTasks();
    }
    
    /**
     * Check if a task is active (first 3 uncompleted tasks)
     */
    public boolean isTaskActive(int taskId) {
        List<ClientTask> uncompleted = getUncompletedTasks();
        for (int i = 0; i < Math.min(ACTIVE_TASK_COUNT, uncompleted.size()); i++) {
            if (uncompleted.get(i).getId() == taskId) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Legacy method for compatibility
     */
    public boolean isTaskActive(int currentLevel, int taskId) {
        return isTaskActive(taskId);
    }

    public ClientTask getTask(int id) {
        if (id > 0 && id <= allTasks.size()) {
            return allTasks.get(id - 1);
        }
        return null;
    }

    public int getTotalTasks() {
        return allTasks.size();
    }

    /**
     * Simple task data class for client-side display
     */
    public static class ClientTask {
        private final int id;
        private final String translationKey;
        private final int targetAmount;
        private int currentProgress;

        public ClientTask(int id, String translationKey, int targetAmount) {
            this.id = id;
            this.translationKey = translationKey;
            this.targetAmount = targetAmount;
            this.currentProgress = 0;
        }

        public int getId() {
            return id;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public int getTargetAmount() {
            return targetAmount;
        }

        public int getCurrentProgress() {
            return currentProgress;
        }

        public void setCurrentProgress(int progress) {
            this.currentProgress = Math.min(progress, targetAmount);
        }

        public float getProgressPercentage() {
            return targetAmount > 0 ? (float) currentProgress / targetAmount : 0f;
        }

        public boolean isCompleted() {
            return currentProgress >= targetAmount;
        }
    }
}

