package com.champ.automine;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class AutoMineKeybindsScreen extends Screen {
    private static final int ACCENT = 0xFF7C5CFF;
    private static final int TEXT = 0xFFF2F0FF;
    private static final int MUTED = 0xFFAAA4C0;
    private static final int PANEL = 0xFF14111F;
    private static final int BORDER = 0xFF322B4C;

    private final Screen parent;
    private final KeyBinding[] keys;
    private final int[] defaults;
    private final ButtonWidget[] buttons = new ButtonWidget[2];
    private int listening = -1;

    public AutoMineKeybindsScreen(Screen parent) {
        super(Text.literal("Champ Auto Mine Keybinds"));
        this.parent = parent;
        this.keys = new KeyBinding[] {AutoMineMod.openKey(), AutoMineMod.toggleKey()};
        this.defaults = new int[] {GLFW.GLFW_KEY_F6, GLFW.GLFW_KEY_F7};
    }

    @Override
    protected void init() {
        int panelW = Math.min(330, width - 16);
        int panelH = 148;
        int x = width / 2 - panelW / 2;
        int y = (height - panelH) / 2;
        int half = (panelW - 44) / 2;
        for (int i = 0; i < keys.length; i++) {
            final int index = i;
            buttons[i] = addDrawableChild(ButtonWidget.builder(Text.literal(""), button -> {
                listening = index;
                refresh();
            }).dimensions(x + 22, y + 38 + i * 24, panelW - 44, 20).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset keys"), button -> {
            for (int i = 0; i < keys.length; i++) {
                keys[i].setBoundKey(InputUtil.Type.KEYSYM.createFromCode(defaults[i]));
            }
            KeyBinding.updateKeysByCode();
            if (client != null) client.options.write();
            listening = -1;
            refresh();
        }).dimensions(x + 22, y + 112, half, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> close())
            .dimensions(x + 22 + half + 8, y + 112, half, 18).build());
        refresh();
    }

    private void refresh() {
        String[] labels = {"Open menu", "Start / stop"};
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == null) continue;
            String value = listening == i ? "Press a key..." : keys[i].getBoundKeyLocalizedText().getString();
            buttons[i].setMessage(Text.literal(labels[i] + ": " + value));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        int panelW = Math.min(330, width - 16);
        int panelH = 148;
        int x = width / 2 - panelW / 2;
        int y = (height - panelH) / 2;
        context.fill(x, y, x + panelW, y + panelH, PANEL);
        context.fill(x, y, x + panelW, y + 2, ACCENT);
        context.fill(x, y + panelH - 1, x + panelW, y + panelH, BORDER);
        context.drawText(this.textRenderer, this.title, x + 22, y + 10, TEXT, false);
        context.drawText(this.textRenderer, Text.literal("Click a binding and press a key. Escape cancels."),
            x + 22, y + 23, MUTED, false);
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(0, 0, width, height, 0xA6000000);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (listening >= 0) {
            if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
                listening = -1;
                refresh();
                return true;
            }
            keys[listening].setBoundKey(InputUtil.Type.KEYSYM.createFromCode(input.key()));
            KeyBinding.updateKeysByCode();
            if (client != null) client.options.write();
            listening = -1;
            refresh();
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
