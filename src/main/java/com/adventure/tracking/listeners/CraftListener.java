package com.adventure.tracking.listeners;

import com.adventure.AdventureMod;
import com.adventure.task.Task;
import com.adventure.task.TaskManager;
import com.adventure.task.TaskType;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class CraftListener {
    public static void register() {
        // TODO: Implement proper crafting event listener
        // For now, using UseItemCallback as placeholder
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient() && player instanceof ServerPlayerEntity) {
                checkCrafting((ServerPlayerEntity) player, player.getStackInHand(hand));
            }
            return ActionResult.PASS;
        });
    }

    private static void checkCrafting(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        Task activeTask = TaskManager.getInstance().getActiveTask(player);
        if (activeTask != null && activeTask.getType() == TaskType.CRAFT_ITEM) {
            if (activeTask.matchesItem(stack)) {
                int amount = stack.getCount();
                activeTask.addProgress(amount);
                
                if (activeTask.isCompleted()) {
                    TaskManager.getInstance().completeTask(player, activeTask);
                    com.adventure.reward.RewardGiver.giveReward(player, activeTask);
                    AdventureMod.LOGGER.info("Task {} completed by player {}", activeTask.getId(), player.getName().getString());
                }
            }
        }
    }
}

