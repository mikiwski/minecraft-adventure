package com.adventure;

import com.adventure.config.ModConfig;
import com.adventure.ui.TaskHudOverlay;
import com.adventure.ui.TaskListScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AdventureModClient implements ClientModInitializer {
    private static KeyBinding openTaskListKey;

    @Override
    public void onInitializeClient() {
        AdventureMod.LOGGER.info("Initializing Adventure Tasks Mod Client");

        // Register key binding
        openTaskListKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.adventure.open_task_list",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                KeyBinding.Category.MISC
        ));

        // Register HUD overlay
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            TaskHudOverlay.render(drawContext, tickCounter);
        });

        // Register key press handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openTaskListKey.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new TaskListScreen());
                }
            }
        });

        // Initialize config
        ModConfig.initialize();

        AdventureMod.LOGGER.info("Adventure Tasks Mod Client initialized successfully");
    }
}
