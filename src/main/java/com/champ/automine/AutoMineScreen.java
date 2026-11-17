package com.champ.automine;

import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class AutoMineScreen extends Screen {
    private static final int ACCENT = 0xFF7C5CFF;
    private static final int TEXT = 0xFFF2F0FF;
    private static final int MUTED = 0xFFAAA4C0;
    private static final int PANEL = 0xFF14111F;
    private static final int BORDER = 0xFF322B4C;

    private final Screen parent;
    private ButtonWidget stateButton;
    private ButtonWidget lockPitchButton;
    private ButtonWidget lockYawButton;
    private ButtonWidget forwardButton;
    private ButtonWidget breakButton;
    private ButtonWidget lavaButton;
    private ButtonWidget humanButton;

    public AutoMineScreen() {
        this(null);
    }

    public AutoMineScreen(Screen parent) {
        super(Text.literal("Champ Auto Mine"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int panelW = Math.min(400, width - 16);
        int panelH = 214;
        int x = width / 2 - panelW / 2;
        int y = (height - panelH) / 2;
        int gap = 6;
        int half = (panelW - 44 - gap) / 2;

        stateButton = addDrawableChild(ButtonWidget.builder(Text.literal("Auto Mine: OFF"), button -> {
            AutoMineController.toggle(client);
            refresh();
        }).dimensions(x + 22, y + 32, panelW - 44, 20).build());

        lockPitchButton = addDrawableChild(ButtonWidget.builder(Text.literal("Lock Pitch"), button -> {
            AutoMineConfig.lockPitch = !AutoMineConfig.lockPitch;
            refresh();
        }).dimensions(x + 22, y + 70, half, 18).build());
        lockYawButton = addDrawableChild(ButtonWidget.builder(Text.literal("Lock Yaw"), button -> {
            AutoMineConfig.lockYaw = !AutoMineConfig.lockYaw;
            refresh();
        }).dimensions(x + 22 + half + gap, y + 70, half, 18).build());
        forwardButton = addDrawableChild(ButtonWidget.builder(Text.literal("Auto Forward"), button -> {
            AutoMineConfig.autoForward = !AutoMineConfig.autoForward;
            refresh();
        }).dimensions(x + 22, y + 90, half, 18).build());
        breakButton = addDrawableChild(ButtonWidget.builder(Text.literal("Auto Break"), button -> {
            AutoMineConfig.autoBreak = !AutoMineConfig.autoBreak;
            refresh();
        }).dimensions(x + 22 + half + gap, y + 90, half, 18).build());
        lavaButton = addDrawableChild(ButtonWidget.builder(Text.literal("Avoid Lava"), button -> {
            AutoMineConfig.avoidLava = !AutoMineConfig.avoidLava;
            refresh();
        }).dimensions(x + 22, y + 110, half, 18).build());
        humanButton = addDrawableChild(ButtonWidget.builder(Text.literal("Human Mode"), button -> {
            AutoMineConfig.humanMode = !AutoMineConfig.humanMode;
            refresh();
        }).dimensions(x + 22 + half + gap, y + 110, half, 18).build());

        addDrawableChild(new FloatSlider(x + 22, y + 136, panelW - 44, 18,
            "Pitch", AutoMineConfig.pitch, -90.0F, 90.0F, value -> AutoMineConfig.pitch = value));
        addDrawableChild(new IntSlider(x + 22, y + 158, panelW - 44, 18,
            "Scan Range", AutoMineConfig.scanRange, 1, 8, value -> AutoMineConfig.scanRange = value));

        addDrawableChild(ButtonWidget.builder(Text.literal("Keybinds..."), button ->
            client.setScreen(new AutoMineKeybindsScreen(this)))
            .dimensions(x + 22, y + 184, half, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> close())
            .dimensions(x + 22 + half + gap, y + 184, half, 18).build());
        refresh();
    }

    private void refresh() {
        stateButton.setMessage(Text.literal(AutoMineConfig.enabled ? "Auto Mine: ON" : "Auto Mine: OFF"));
        lockPitchButton.setMessage(Text.literal(AutoMineConfig.lockPitch ? "[x] Lock Pitch" : "[ ] Lock Pitch"));
        lockYawButton.setMessage(Text.literal(AutoMineConfig.lockYaw ? "[x] Lock Yaw" : "[ ] Lock Yaw"));
        forwardButton.setMessage(Text.literal(AutoMineConfig.autoForward ? "[x] Auto Forward" : "[ ] Auto Forward"));
        breakButton.setMessage(Text.literal(AutoMineConfig.autoBreak ? "[x] Auto Break" : "[ ] Auto Break"));
        lavaButton.setMessage(Text.literal(AutoMineConfig.avoidLava ? "[x] Avoid Lava" : "[ ] Avoid Lava"));
        humanButton.setMessage(Text.literal(AutoMineConfig.humanMode ? "[x] Human Mode" : "[ ] Human Mode"));
    }

    private static String keyName(net.minecraft.client.option.KeyBinding binding) {
        return binding == null ? "unbound" : binding.getBoundKeyLocalizedText().getString();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        int panelW = Math.min(400, width - 16);
        int panelH = 214;
        int x = width / 2 - panelW / 2;
        int y = (height - panelH) / 2;
        context.fill(x, y, x + panelW, y + panelH, PANEL);
        context.fill(x, y, x + panelW, y + 2, ACCENT);
        context.fill(x, y + panelH - 1, x + panelW, y + panelH, BORDER);
        context.drawText(this.textRenderer, this.title, x + 22, y + 9, TEXT, false);
        context.drawText(this.textRenderer, Text.literal(
            "Human-friendly mining - " + keyName(AutoMineMod.openKey()) + " opens, "
                + keyName(AutoMineMod.toggleKey()) + " toggles"), x + 22, y + 20, MUTED, false);
        context.drawText(this.textRenderer, Text.literal(AutoMineController.status()), x + 22, y + 57, MUTED, false);
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(0, 0, width, height, 0xA6000000);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void close() {
        AutoMineConfig.save();
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static final class FloatSlider extends SliderWidget {
        private final String label;
        private final float min;
        private final float max;
        private final Consumer<Float> setter;

        private FloatSlider(int x, int y, int w, int h, String label, float current, float min, float max,
                            Consumer<Float> setter) {
            super(x, y, w, h, Text.empty(), (current - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        private float current() {
            return min + (float) value * (max - min);
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(label + ": " + String.format(Locale.ROOT, "%.1f", current())));
        }

        @Override
        protected void applyValue() {
            setter.accept(current());
        }
    }

    private static final class IntSlider extends SliderWidget {
        private final String label;
        private final int min;
        private final int max;
        private final Consumer<Integer> setter;

        private IntSlider(int x, int y, int w, int h, String label, int current, int min, int max,
                          Consumer<Integer> setter) {
            super(x, y, w, h, Text.empty(), (current - min) / (double) (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        private int current() {
            return min + (int) Math.round(value * (max - min));
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(label + ": " + current()));
        }

        @Override
        protected void applyValue() {
            setter.accept(current());
        }
    }
}
