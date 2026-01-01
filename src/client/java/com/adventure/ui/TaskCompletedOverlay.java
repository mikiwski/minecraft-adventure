package com.adventure.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Animated overlay shown when a task is completed
 * Features fade in, hold, and fade out animation
 */
public class TaskCompletedOverlay {
    private static TaskCompletedOverlay instance;
    
    private boolean active = false;
    private long startTime = 0;
    private String taskName = "";
    private int xpReward = 0;
    private List<ItemReward> itemRewards = new ArrayList<>();
    
    private record ItemReward(ItemStack stack, int count) {}
    
    // Animation timing (in milliseconds)
    private static final long FADE_IN_DURATION = 300;
    private static final long HOLD_DURATION = 2400;
    private static final long FADE_OUT_DURATION = 300;
    private static final long TOTAL_DURATION = FADE_IN_DURATION + HOLD_DURATION + FADE_OUT_DURATION;
    
    public static TaskCompletedOverlay getInstance() {
        if (instance == null) {
            instance = new TaskCompletedOverlay();
        }
        return instance;
    }
    
    public void show(String taskTranslationKey, int xpReward, String itemsData) {
        this.active = true;
        this.startTime = System.currentTimeMillis();
        this.taskName = taskTranslationKey;
        this.xpReward = xpReward;
        
        // Parse items data
        this.itemRewards.clear();
        if (itemsData != null && !itemsData.isEmpty()) {
            String[] items = itemsData.split(",");
            for (String itemStr : items) {
                String[] parts = itemStr.split(":");
                if (parts.length >= 3) {
                    String itemId = parts[0] + ":" + parts[1]; // e.g. "minecraft:iron_ingot"
                    int count = Integer.parseInt(parts[2]);
                    
                    try {
                        Item item = Registries.ITEM.get(Identifier.of(itemId));
                        if (item != null) {
                            itemRewards.add(new ItemReward(new ItemStack(item, count), count));
                        }
                    } catch (Exception e) {
                        // Ignore invalid items
                    }
                }
            }
        }
    }
    
    public void render(DrawContext context, float tickDelta) {
        if (!active) return;
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        if (elapsed >= TOTAL_DURATION) {
            active = false;
            return;
        }
        
        // Calculate alpha based on animation phase
        float alpha = calculateAlpha(elapsed);
        if (alpha <= 0) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) return;
        
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Box dimensions - taller to fit items
        int boxWidth = 280;
        int boxHeight = itemRewards.isEmpty() ? 70 : 95;
        int boxX = (screenWidth - boxWidth) / 2;
        int boxY = screenHeight / 4;
        
        int alphaInt = (int)(alpha * 255);
        
        // Draw background with alpha
        int bgColor = (alphaInt << 24) | 0x1A1A2E;
        context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, bgColor);
        
        // Draw golden border
        int borderColor = ((int)(alpha * 200) << 24) | 0xFFD700;
        int borderThickness = 3;
        // Top
        context.fill(boxX, boxY, boxX + boxWidth, boxY + borderThickness, borderColor);
        // Bottom
        context.fill(boxX, boxY + boxHeight - borderThickness, boxX + boxWidth, boxY + boxHeight, borderColor);
        // Left
        context.fill(boxX, boxY, boxX + borderThickness, boxY + boxHeight, borderColor);
        // Right
        context.fill(boxX + boxWidth - borderThickness, boxY, boxX + boxWidth, boxY + boxHeight, borderColor);
        
        // Draw "TASK COMPLETED!" header
        Text headerText = Text.literal("✦ ZADANIE WYKONANE! ✦");
        int headerColor = (alphaInt << 24) | 0xFFD700;
        int headerWidth = client.textRenderer.getWidth(headerText);
        context.drawText(client.textRenderer, headerText, 
            boxX + (boxWidth - headerWidth) / 2, boxY + 10, headerColor, true);
        
        // Draw task name
        Text taskText = Text.translatable(taskName);
        int taskColor = (alphaInt << 24) | 0xFFFFFF;
        int taskWidth = client.textRenderer.getWidth(taskText);
        context.drawText(client.textRenderer, taskText, 
            boxX + (boxWidth - taskWidth) / 2, boxY + 26, taskColor, true);
        
        // Draw XP reward
        Text xpText = Text.literal("+" + xpReward + " XP");
        int xpColor = (alphaInt << 24) | 0x55FF55;
        int xpWidth = client.textRenderer.getWidth(xpText);
        context.drawText(client.textRenderer, xpText, 
            boxX + (boxWidth - xpWidth) / 2, boxY + 42, xpColor, true);
        
        // Draw item rewards
        if (!itemRewards.isEmpty()) {
            int itemY = boxY + 58;
            int totalItemsWidth = itemRewards.size() * 22;
            int itemStartX = boxX + (boxWidth - totalItemsWidth) / 2;
            
            for (int i = 0; i < itemRewards.size(); i++) {
                ItemReward reward = itemRewards.get(i);
                int itemX = itemStartX + i * 22;
                
                // Draw item icon
                context.drawItem(reward.stack, itemX, itemY);
                
                // Draw count next to item
                String countStr = "x" + reward.count;
                int countColor = (alphaInt << 24) | 0xFFFFFF;
                context.drawText(client.textRenderer, countStr, 
                    itemX + 17, itemY + 5, countColor, true);
            }
        }
        
        // Draw decorative particles effect (simple dots)
        drawParticles(context, boxX, boxY, boxWidth, boxHeight, alpha, elapsed);
    }
    
    private float calculateAlpha(long elapsed) {
        if (elapsed < FADE_IN_DURATION) {
            // Fade in - ease out curve
            float progress = (float) elapsed / FADE_IN_DURATION;
            return easeOutCubic(progress);
        } else if (elapsed < FADE_IN_DURATION + HOLD_DURATION) {
            // Hold at full opacity
            return 1.0f;
        } else {
            // Fade out - ease in curve
            float progress = (float) (elapsed - FADE_IN_DURATION - HOLD_DURATION) / FADE_OUT_DURATION;
            return 1.0f - easeInCubic(progress);
        }
    }
    
    private float easeOutCubic(float x) {
        return 1.0f - (float) Math.pow(1 - x, 3);
    }
    
    private float easeInCubic(float x) {
        return x * x * x;
    }
    
    private void drawParticles(DrawContext context, int boxX, int boxY, int boxWidth, int boxHeight, float alpha, long elapsed) {
        // Simple sparkle effect around the border
        int particleCount = 8;
        long animCycle = elapsed % 1000;
        
        for (int i = 0; i < particleCount; i++) {
            float angle = (float) ((i * 360.0 / particleCount) + (animCycle * 0.36));
            float radians = (float) Math.toRadians(angle);
            
            int centerX = boxX + boxWidth / 2;
            int centerY = boxY + boxHeight / 2;
            
            int particleX = centerX + (int) (Math.cos(radians) * (boxWidth / 2 + 5));
            int particleY = centerY + (int) (Math.sin(radians) * (boxHeight / 2 + 5));
            
            // Sparkle alpha varies
            float sparkleAlpha = alpha * (0.5f + 0.5f * (float) Math.sin(elapsed * 0.01 + i));
            int sparkleColor = ((int)(sparkleAlpha * 255) << 24) | 0xFFD700;
            
            context.fill(particleX - 1, particleY - 1, particleX + 2, particleY + 2, sparkleColor);
        }
    }
    
    public boolean isActive() {
        return active;
    }
}

