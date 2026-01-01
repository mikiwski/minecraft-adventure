package com.adventure.reward;

import com.adventure.AdventureMod;
import com.adventure.task.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RewardGiver {
    private static final Random random = new Random();
    
    /**
     * Reward result containing XP and items
     */
    public record RewardResult(int xp, List<ItemReward> items) {
        public record ItemReward(String itemId, int count) {}
    }

    /**
     * Give reward to player for completing a task
     * @return RewardResult with XP and items
     */
    public static RewardResult giveReward(ServerPlayerEntity player, Task task) {
        // Give XP reward
        int xp = RewardCalculator.calculateXPReward(task);
        player.addExperience(xp);
        
        // Give item rewards based on difficulty
        int itemMultiplier = RewardCalculator.calculateItemRewardMultiplier(task);
        List<RewardResult.ItemReward> itemRewards = giveItemRewards(player, task, itemMultiplier);
        
        AdventureMod.LOGGER.info("Reward given to player {} for task {}: {} XP, {} items", 
            player.getName().getString(), task.getId(), xp, itemRewards.size());
        
        return new RewardResult(xp, itemRewards);
    }

    private static List<RewardResult.ItemReward> giveItemRewards(ServerPlayerEntity player, Task task, int multiplier) {
        List<RewardResult.ItemReward> rewards = new ArrayList<>();
        
        // Give random items based on difficulty
        for (int i = 0; i < multiplier; i++) {
            ItemStack reward = getRandomRewardItem(task.getDifficulty());
            if (!reward.isEmpty()) {
                // Track for display
                String itemId = Registries.ITEM.getId(reward.getItem()).toString();
                rewards.add(new RewardResult.ItemReward(itemId, reward.getCount()));
                
                if (!player.getInventory().insertStack(reward)) {
                    // Drop if inventory is full
                    player.dropItem(reward, false);
                }
            }
        }
        
        return rewards;
    }

    private static ItemStack getRandomRewardItem(int difficulty) {
        // Simple reward system - can be expanded
        if (difficulty <= 5) {
            return new ItemStack(Items.IRON_INGOT, 1 + random.nextInt(3));
        } else if (difficulty <= 10) {
            return new ItemStack(Items.GOLD_INGOT, 1 + random.nextInt(3));
        } else if (difficulty <= 20) {
            return new ItemStack(Items.DIAMOND, 1 + random.nextInt(2));
        } else {
            return new ItemStack(Items.EMERALD, 1 + random.nextInt(3));
        }
    }
}

