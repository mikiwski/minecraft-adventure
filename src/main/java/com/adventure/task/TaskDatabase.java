package com.adventure.task;

import com.adventure.task.tasks.CollectItemTask;
import com.adventure.task.tasks.CraftItemTask;
import com.adventure.task.tasks.KillMobTask;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabase {
    private final List<Task> allTasks;

    public TaskDatabase() {
        this.allTasks = new ArrayList<>();
        initializeTasks();
    }

    private void initializeTasks() {
        // TODO: Implement all 200 tasks
        // For now, add a few placeholder tasks to make the project compile
        allTasks.add(new CollectItemTask(1, 1, 10, null, TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "logs")), "task.adventure.collect_logs"));
        allTasks.add(new CollectItemTask(2, 2, 20, null, TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "planks")), "task.adventure.collect_planks"));
    }

    public List<Task> getAllTasks() {
        return allTasks;
    }

    public Task getTask(int id) {
        if (id > 0 && id <= allTasks.size()) {
            return allTasks.get(id - 1);
        }
        return null;
    }
}

