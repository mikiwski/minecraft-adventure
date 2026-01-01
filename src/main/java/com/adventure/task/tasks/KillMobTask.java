package com.adventure.task.tasks;

import com.adventure.task.Task;
import com.adventure.task.TaskType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class KillMobTask extends Task {
    private final EntityType<?> targetEntityType;
    private final TagKey<EntityType<?>> entityTag;

    public KillMobTask(int id, int difficulty, int targetAmount, Identifier targetEntity, TagKey<EntityType<?>> entityTag, String translationKey) {
        super(id, TaskType.KILL_MOB, difficulty, targetAmount, targetEntity, entityTag, translationKey);
        this.targetEntityType = null; // Will be resolved from identifier if needed
        this.entityTag = entityTag;
    }

    @Override
    public boolean matchesTarget(Object target) {
        if (target instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) target;
            if (targetEntityType != null && entity.getType() == targetEntityType) {
                return true;
            }
            if (entityTag != null) {
                return entity.getType().isIn(entityTag);
            }
        }
        return false;
    }
}

