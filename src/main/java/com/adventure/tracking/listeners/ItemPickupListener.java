package com.adventure.tracking.listeners;

import com.adventure.AdventureMod;
import com.adventure.task.Task;
import com.adventure.task.TaskManager;
import com.adventure.task.TaskType;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;

public class ItemPickupListener {
    public static void register() {
        // Listen to inventory changes
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity) {
                checkItemPickup((ServerPlayerEntity) player, player.getStackInHand(hand));
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
    }

    private static void checkItemPickup(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        Task activeTask = TaskManager.getInstance().getActiveTask(player);
        if (activeTask != null && activeTask.getType() == TaskType.COLLECT_ITEM) {
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

