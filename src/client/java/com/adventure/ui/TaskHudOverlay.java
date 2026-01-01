package com.adventure.ui;

import com.adventure.config.ModConfig;
import com.adventure.data.ClientTaskCache;
import com.adventure.data.ClientTaskDatabase;
import com.adventure.data.ClientTaskDatabase.ClientTask;
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

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!ModConfig.isHudEnabled()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        // Don't render when chat or other screens are open
        if (client.currentScreen != null) {
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Render key binding hint at top center
        renderKeyHint(context, screenWidth);
        
        // Render position (right side)
        int x = screenWidth - TASK_WIDTH - OFFSET_X;
        int y = OFFSET_Y + 25; // Below the hint

        // Get uncompleted tasks from client database
        List<ClientTask> tasks = ClientTaskDatabase.getInstance().getUncompletedTasks();
        
        if (tasks.isEmpty()) {
            return;
        }

        // Render each task - first 3 are active
        for (int i = 0; i < Math.min(5, tasks.size()); i++) {
            ClientTask task = tasks.get(i);
            // Task is active if it's one of the first 3 uncompleted
            boolean isActive = i < ClientTaskDatabase.ACTIVE_TASK_COUNT;
            
            renderTask(context, x, y + i * (TASK_HEIGHT + PADDING), task, isActive);
        }
    }

    private static void renderKeyHint(DrawContext context, int screenWidth) {
        // Get the key binding name
        String keyName = "J"; // Default key
        try {
            var keyBinding = com.adventure.AdventureModClient.getOpenTaskListKey();
            if (keyBinding != null) {
                keyName = keyBinding.getBoundKeyLocalizedText().getString();
            }
        } catch (Exception e) {
            // Use default "J" if we can't get the key
        }
        
        // Use translation for hint text
        Text hintText = Text.translatable("hint.adventure.open_task_list", keyName);
        String hintStr = hintText.getString();
        
        int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(hintStr);
        int hintX = screenWidth / 2 - textWidth / 2;
        int hintY = 5;
        
        // Semi-transparent background
        int bgWidth = textWidth + 16;
        int bgHeight = 14;
        context.fill(hintX - 8, hintY - 2, hintX + bgWidth - 8, hintY + bgHeight, 0xCC000000);
        
        // Draw text with shadow - ARGB format with full alpha
        context.drawText(MinecraftClient.getInstance().textRenderer, hintStr, hintX, hintY, 0xFFFFFF55, true);
    }

    private static void renderTask(DrawContext context, int x, int y, ClientTask task, boolean isActive) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Background - blue for active, dark for others
        int bgColor = isActive ? 0xDD1155AA : 0xBB000000;
        context.fill(x, y, x + TASK_WIDTH, y + TASK_HEIGHT, bgColor);
        
        // Border
        int borderColor = isActive ? 0xFFFFFF00 : 0xFF666666;
        // Top
        context.fill(x, y, x + TASK_WIDTH, y + 2, borderColor);
        // Bottom
        context.fill(x, y + TASK_HEIGHT - 2, x + TASK_WIDTH, y + TASK_HEIGHT, borderColor);
        // Left
        context.fill(x, y, x + 2, y + TASK_HEIGHT, borderColor);
        // Right
        context.fill(x + TASK_WIDTH - 2, y, x + TASK_WIDTH, y + TASK_HEIGHT, borderColor);
        
        // Level number - use String directly with ARGB colors (0xFF prefix for full alpha)
        String levelText = "Lv." + task.getId();
        context.drawText(client.textRenderer, levelText, x + 6, y + 6, isActive ? 0xFFFFFF00 : 0xFFAAAAAA, true);
        
        // Task name - get translated or use key
        String taskName;
        Text translatedText = Text.translatable(task.getTranslationKey());
        String translatedStr = translatedText.getString();
        // If translation failed, it returns the key - use a fallback
        if (translatedStr.equals(task.getTranslationKey()) || translatedStr.isEmpty()) {
            // Extract simple name from key like "task.adventure.collect_logs" -> "Collect Logs"
            String key = task.getTranslationKey();
            int lastDot = key.lastIndexOf('.');
            taskName = lastDot >= 0 ? key.substring(lastDot + 1).replace('_', ' ') : key;
        } else {
            taskName = translatedStr;
        }
        context.drawText(client.textRenderer, taskName, x + 40, y + 6, isActive ? 0xFFFFFFFF : 0xFFCCCCCC, true);
        
        // Progress text
        String progressText = task.getCurrentProgress() + " / " + task.getTargetAmount();
        context.drawText(client.textRenderer, progressText, x + 6, y + 20, isActive ? 0xFF55FF55 : 0xFF888888, true);
        
        // Progress bar for active task
        if (isActive) {
            int barWidth = TASK_WIDTH - 12;
            int barHeight = 8;
            int barX = x + 6;
            int barY = y + TASK_HEIGHT - 12;
            
            // Background bar
            context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
            
            // Progress bar (green)
            float progress = task.getProgressPercentage();
            int progressWidth = (int) (barWidth * progress);
            if (progressWidth > 0) {
                context.fill(barX, barY, barX + progressWidth, barY + barHeight, 0xFF00DD00);
            }
            
            // Bar border
            context.fill(barX, barY, barX + barWidth, barY + 1, 0xFF555555);
            context.fill(barX, barY + barHeight - 1, barX + barWidth, barY + barHeight, 0xFF555555);
        }
    }
}
