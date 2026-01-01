package com.adventure.ui;

import com.adventure.config.ModConfig;
import com.adventure.data.ClientTaskCache;
import com.adventure.task.Task;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TaskHudOverlay {
    private static final int TASK_WIDTH = 200;
    private static final int TASK_HEIGHT = 40;
    private static final int PADDING = 5;
    private static final int OFFSET_X = 10;
    private static final int OFFSET_Y = 10;
    private static final int HINT_HEIGHT = 20;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!ModConfig.isHudEnabled()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Render position (right side by default)
        int x = screenWidth - TASK_WIDTH - OFFSET_X;
        int y = OFFSET_Y;

        // Get player tasks from client cache
        List<Task> tasks = ClientTaskCache.getInstance().getPlayerTasks();
        
        // Render key binding hint at the bottom
        renderKeyHint(context, screenWidth, screenHeight);
        
        if (tasks.isEmpty()) {
            return;
        }

        // Render each task
        for (int i = 0; i < Math.min(5, tasks.size()); i++) {
            Task task = tasks.get(i);
            int currentLevel = ClientTaskCache.getInstance().getCurrentLevel();
            boolean isActive = (task.getId() == currentLevel);
            
            renderTask(context, x, y + i * (TASK_HEIGHT + PADDING), task, isActive);
        }
    }

    private static void renderKeyHint(DrawContext context, int screenWidth, int screenHeight) {
        // Get the key binding name
        String keyName = "T"; // Default key
        try {
            var keyBinding = com.adventure.AdventureModClient.getOpenTaskListKey();
            if (keyBinding != null) {
                keyName = keyBinding.getBoundKeyLocalizedText().getString();
            }
        } catch (Exception e) {
            // Use default "T" if we can't get the key
        }
        
        // Render hint text at bottom center
        Text hintText = Text.translatable("hint.adventure.open_task_list", keyName)
                .formatted(Formatting.GRAY, Formatting.ITALIC);
        
        int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(hintText);
        int hintX = screenWidth / 2 - textWidth / 2;
        int hintY = screenHeight - 30;
        
        // Semi-transparent background
        int bgWidth = textWidth + 10;
        int bgHeight = HINT_HEIGHT;
        context.fill(hintX - 5, hintY - 2, hintX + bgWidth - 5, hintY + bgHeight - 2, 0x80000000);
        
        // Draw text
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, hintText, hintX, hintY, 0xCCCCCC);
    }

    private static void renderTask(DrawContext context, int x, int y, Task task, boolean isActive) {
        // Background
        int bgColor = isActive ? 0x80000000 | 0x0088FF : 0x80000000;
        context.fill(x, y, x + TASK_WIDTH, y + TASK_HEIGHT, bgColor);
        
        // Border (draw manually since drawBorder doesn't exist in 1.21.x)
        int borderColor = isActive ? 0xFFFFFFFF : 0xFF888888;
        // Top
        context.fill(x, y, x + TASK_WIDTH, y + 1, borderColor);
        // Bottom
        context.fill(x, y + TASK_HEIGHT - 1, x + TASK_WIDTH, y + TASK_HEIGHT, borderColor);
        // Left
        context.fill(x, y, x + 1, y + TASK_HEIGHT, borderColor);
        // Right
        context.fill(x + TASK_WIDTH - 1, y, x + TASK_WIDTH, y + TASK_HEIGHT, borderColor);
        
        // Task text
        Text taskText = Text.translatable(task.getTranslationKey())
                .formatted(isActive ? Formatting.YELLOW : Formatting.WHITE);
        context.drawText(MinecraftClient.getInstance().textRenderer, taskText, 
                x + 5, y + 5, 0xFFFFFF, false);
        
        // Progress text
        String progressText = task.getCurrentProgress() + "/" + task.getTargetAmount();
        context.drawText(MinecraftClient.getInstance().textRenderer, progressText,
                x + 5, y + 20, 0xCCCCCC, false);
        
        // Progress bar
        if (isActive && task.getTargetAmount() > 0) {
            int barWidth = TASK_WIDTH - 10;
            int barHeight = 6;
            int barX = x + 5;
            int barY = y + TASK_HEIGHT - 12;
            
            // Background bar
            context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
            
            // Progress bar
            float progress = task.getProgressPercentage();
            int progressWidth = (int) (barWidth * progress);
            context.fill(barX, barY, barX + progressWidth, barY + barHeight, 0xFF00FF00);
        }
    }
}
