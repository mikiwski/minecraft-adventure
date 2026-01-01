package com.adventure;

import com.adventure.config.ModConfig;
import com.adventure.network.ClientTaskSyncHandler;
import com.adventure.ui.TaskCompletedOverlay;
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

    public static KeyBinding getOpenTaskListKey() {
        return openTaskListKey;
    }

    @Override
    public void onInitializeClient() {
        AdventureMod.LOGGER.info("Initializing Adventure Tasks Mod Client");

        // Register network handler for task sync
        ClientTaskSyncHandler.register();

        // Register key binding - using J key (T is chat)
        openTaskListKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.adventure.open_task_list",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                KeyBinding.Category.GAMEPLAY
        ));

        // Register HUD overlay
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            TaskHudOverlay.render(drawContext, tickCounter);
            // Use 0 as tickDelta - the overlay uses system time for animation
            TaskCompletedOverlay.getInstance().render(drawContext, 0);
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
