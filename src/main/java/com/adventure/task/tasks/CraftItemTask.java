package com.adventure.task.tasks;

import com.adventure.task.Task;
import com.adventure.task.TaskType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CraftItemTask extends Task {
    public CraftItemTask(int id, int difficulty, int targetAmount, Identifier targetItem, TagKey<Item> targetTag, String translationKey) {
        super(id, TaskType.CRAFT_ITEM, difficulty, targetAmount, targetItem, targetTag, translationKey);
    }

    @Override
    public boolean matchesTarget(Object target) {
        if (target instanceof ItemStack) {
            return matchesItem((ItemStack) target);
        }
        return false;
    }
}

