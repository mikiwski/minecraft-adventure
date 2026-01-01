package com.adventure.task;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryWrapper;

public abstract class Task {
    protected final int id;
    protected final TaskType type;
    protected final int difficulty;
    protected final int targetAmount;
    protected int currentProgress;
    protected final Identifier targetItem;
    protected final TagKey<Item> targetTag;
    protected final String translationKey;

    public Task(int id, TaskType type, int difficulty, int targetAmount, Identifier targetItem, TagKey<Item> targetTag, String translationKey) {
        this.id = id;
        this.type = type;
        this.difficulty = difficulty;
        this.targetAmount = targetAmount;
        this.currentProgress = 0;
        this.targetItem = targetItem;
        this.targetTag = targetTag;
        this.translationKey = translationKey;
    }

    public int getId() {
        return id;
    }

    public TaskType getType() {
        return type;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int progress) {
        this.currentProgress = Math.max(0, Math.min(progress, targetAmount));
    }

    public void addProgress(int amount) {
        setCurrentProgress(currentProgress + amount);
    }

    public boolean isCompleted() {
        return currentProgress >= targetAmount;
    }

    public float getProgressPercentage() {
        return targetAmount > 0 ? (float) currentProgress / targetAmount : 0.0f;
    }

    public Identifier getTargetItem() {
        return targetItem;
    }

    public TagKey<Item> getTargetTag() {
        return targetTag;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    /**
     * Sprawdza czy przedmiot pasuje do zadania (bezpośrednio lub przez tag)
     */
    public boolean matchesItem(ItemStack stack) {
        if (targetItem != null) {
            Identifier itemId = stack.getItem().getRegistryEntry().getId();
            if (itemId != null && itemId.equals(targetItem)) {
                return true;
            }
        }
        if (targetTag != null) {
            return stack.isIn(targetTag);
        }
        return false;
    }

    public abstract boolean matchesTarget(Object target);
}

