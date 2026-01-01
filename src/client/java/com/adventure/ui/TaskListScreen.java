package com.adventure.ui;

import com.adventure.data.ClientTaskCache;
import com.adventure.data.ClientTaskDatabase;
import com.adventure.data.ClientTaskDatabase.ClientTask;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TaskListScreen extends Screen {
    private static final int TASK_ENTRY_HEIGHT = 30;
    private static final int TASK_LIST_WIDTH = 300;

    public TaskListScreen() {
        super(Text.translatable("screen.adventure.task_list"));
    }

    @Override
    protected void init() {
        // Close button
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.adventure.close"),
                button -> this.close()
        ).dimensions(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, 
                Text.translatable("screen.adventure.task_list").formatted(Formatting.GOLD, Formatting.BOLD), 
                this.width / 2, 15, 0xFFFFAA);
        
        // Get current level and tasks
        int currentLevel = ClientTaskCache.getInstance().getCurrentLevel();
        int totalTasks = ClientTaskDatabase.getInstance().getTotalTasks();
        
        // Progress info
        String progressInfo = String.format("Poziom: %d / %d", currentLevel, totalTasks);
        context.drawCenteredTextWithShadow(this.textRenderer, progressInfo, 
                this.width / 2, 30, 0xAAAAAA);
        
        // Render task list
        int listX = this.width / 2 - TASK_LIST_WIDTH / 2;
        int listY = 50;
        
        List<ClientTask> tasks = ClientTaskDatabase.getInstance().getTasksForLevel(currentLevel);
        
        for (int i = 0; i < tasks.size(); i++) {
            ClientTask task = tasks.get(i);
            boolean isActive = (task.getId() == currentLevel);
            boolean isCompleted = task.getId() < currentLevel;
            
            renderTaskEntry(context, listX, listY + i * TASK_ENTRY_HEIGHT, task, isActive, isCompleted);
        }
        
        // Instructions
        context.drawCenteredTextWithShadow(this.textRenderer, 
                Text.translatable("screen.adventure.instructions").formatted(Formatting.GRAY, Formatting.ITALIC), 
                this.width / 2, this.height - 50, 0x888888);
        
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderTaskEntry(DrawContext context, int x, int y, ClientTask task, boolean isActive, boolean isCompleted) {
        // Background
        int bgColor;
        if (isCompleted) {
            bgColor = 0x88006600; // Green for completed
        } else if (isActive) {
            bgColor = 0xCC1155AA; // Blue for active
        } else {
            bgColor = 0x88333333; // Gray for future
        }
        context.fill(x, y, x + TASK_LIST_WIDTH, y + TASK_ENTRY_HEIGHT - 2, bgColor);
        
        // Border for active
        if (isActive) {
            // Top
            context.fill(x, y, x + TASK_LIST_WIDTH, y + 1, 0xFFFFFF00);
            // Bottom
            context.fill(x, y + TASK_ENTRY_HEIGHT - 3, x + TASK_LIST_WIDTH, y + TASK_ENTRY_HEIGHT - 2, 0xFFFFFF00);
            // Left
            context.fill(x, y, x + 1, y + TASK_ENTRY_HEIGHT - 2, 0xFFFFFF00);
            // Right
            context.fill(x + TASK_LIST_WIDTH - 1, y, x + TASK_LIST_WIDTH, y + TASK_ENTRY_HEIGHT - 2, 0xFFFFFF00);
        }
        
        // Level indicator
        String levelStr = String.format("Lv.%d", task.getId());
        int levelColor = isCompleted ? 0x00FF00 : (isActive ? 0xFFFF00 : 0x888888);
        context.drawTextWithShadow(this.textRenderer, levelStr, x + 5, y + 5, levelColor);
        
        // Task name
        Text taskName = Text.translatable(task.getTranslationKey());
        int nameColor = isCompleted ? 0x88FF88 : (isActive ? 0xFFFFFF : 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, taskName, x + 45, y + 5, nameColor);
        
        // Progress or status
        String statusText;
        if (isCompleted) {
            statusText = "✓ Ukończone";
        } else if (isActive) {
            statusText = task.getCurrentProgress() + "/" + task.getTargetAmount();
        } else {
            statusText = "0/" + task.getTargetAmount();
        }
        int statusColor = isCompleted ? 0x00FF00 : (isActive ? 0x00DD00 : 0x666666);
        context.drawTextWithShadow(this.textRenderer, statusText, x + 45, y + 16, statusColor);
        
        // Progress bar for active task
        if (isActive) {
            int barX = x + TASK_LIST_WIDTH - 105;
            int barY = y + 10;
            int barWidth = 100;
            int barHeight = 8;
            
            // Background
            context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
            
            // Progress
            float progress = task.getProgressPercentage();
            int progressWidth = (int) (barWidth * progress);
            if (progressWidth > 0) {
                context.fill(barX, barY, barX + progressWidth, barY + barHeight, 0xFF00DD00);
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
