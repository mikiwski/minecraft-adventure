package com.adventure.reward;

import com.adventure.AdventureMod;
import com.adventure.task.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Random;

public class RewardGiver {
    private static final Random random = new Random();

    public static void giveReward(ServerPlayerEntity player, Task task) {
        if (task.isCompleted()) {
            // Give XP reward
            int xp = RewardCalculator.calculateXPReward(task);
            player.addExperience(xp);
            
            // Give item rewards based on difficulty
            int itemMultiplier = RewardCalculator.calculateItemRewardMultiplier(task);
            giveItemRewards(player, task, itemMultiplier);
            
            // Notify player
            player.sendMessage(Text.translatable("message.adventure.task_completed", 
                Text.translatable(task.getTranslationKey())), false);
            player.sendMessage(Text.translatable("message.adventure.reward_xp", xp), false);
            
            AdventureMod.LOGGER.info("Reward given to player {} for task {}: {} XP", 
                player.getName().getString(), task.getId(), xp);
        }
    }

    private static void giveItemRewards(ServerPlayerEntity player, Task task, int multiplier) {
        // Give random items based on difficulty
        for (int i = 0; i < multiplier; i++) {
            ItemStack reward = getRandomRewardItem(task.getDifficulty());
            if (!reward.isEmpty()) {
                if (!player.getInventory().insertStack(reward)) {
                    // Drop if inventory is full
                    player.dropItem(reward, false);
                }
            }
        }
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

