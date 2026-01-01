package com.adventure.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TaskListScreen extends Screen {
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
                Text.translatable("screen.adventure.task_list"), 
                this.width / 2, 20, 0xFFFFFF);
        
        // TODO: Render task list, history, and statistics
        // For now, show placeholder text
        context.drawText(this.textRenderer, 
                Text.translatable("screen.adventure.coming_soon"), 
                this.width / 2 - 100, 60, 0xCCCCCC, false);
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        // Close on ESC
        if (keyCode == 256) { // ESC key
            this.close();
            return true;
        }
        return false;
    }
}

