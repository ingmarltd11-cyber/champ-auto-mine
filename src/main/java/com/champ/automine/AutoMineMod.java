package com.champ.automine;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class AutoMineMod implements ClientModInitializer {
    private static final KeyBinding.Category CATEGORY =
        KeyBinding.Category.create(Identifier.of("champ-auto-mine", "main"));
    private static KeyBinding openKey;
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        AutoMineConfig.load();
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.champ-auto-mine.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F6, CATEGORY));
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.champ-auto-mine.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F7, CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(MinecraftClient client) {
        while (openKey.wasPressed()) client.setScreen(new AutoMineScreen());
        while (toggleKey.wasPressed()) AutoMineController.toggle(client);
        AutoMineController.tick(client);
    }
}
