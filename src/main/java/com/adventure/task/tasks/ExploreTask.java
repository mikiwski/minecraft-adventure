package com.adventure.task.tasks;

import com.adventure.task.Task;
import com.adventure.task.TaskType;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class ExploreTask extends Task {
    private final TagKey<Biome> biomeTag;

    public ExploreTask(int id, int difficulty, int targetAmount, Identifier targetBiome, TagKey<Biome> biomeTag, String translationKey) {
        super(id, TaskType.EXPLORE_BIOME, difficulty, targetAmount, targetBiome, null, translationKey);
        this.biomeTag = biomeTag;
    }

    public TagKey<Biome> getBiomeTag() {
        return biomeTag;
    }

    @Override
    public boolean matchesTarget(Object target) {
        // Implementation for biome exploration
        return false;
    }
}
