package com.champ.automine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class AutoMineConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static boolean enabled;
    public static boolean lockPitch = true;
    public static float pitch;
    public static boolean lockYaw = true;
    public static boolean autoForward = true;
    public static boolean autoBreak = true;
    public static boolean avoidLava = true;
    public static int scanRange = 4;

    private AutoMineConfig() {
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("champ-auto-mine.json");
    }

    public static void load() {
        try {
            Path path = file();
            if (!Files.isRegularFile(path)) return;
            Data data = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), Data.class);
            if (data == null) return;
            enabled = data.enabled;
            lockPitch = data.lockPitch;
            pitch = data.pitch;
            lockYaw = data.lockYaw;
            autoForward = data.autoForward;
            autoBreak = data.autoBreak;
            avoidLava = data.avoidLava;
            scanRange = Math.max(1, Math.min(8, data.scanRange));
        } catch (Throwable ignored) {
        }
    }

    public static void save() {
        try {
            Path path = file();
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(new Data()), StandardCharsets.UTF_8);
        } catch (Throwable ignored) {
        }
    }

    private static final class Data {
        boolean enabled = AutoMineConfig.enabled;
        boolean lockPitch = AutoMineConfig.lockPitch;
        float pitch = AutoMineConfig.pitch;
        boolean lockYaw = AutoMineConfig.lockYaw;
        boolean autoForward = AutoMineConfig.autoForward;
        boolean autoBreak = AutoMineConfig.autoBreak;
        boolean avoidLava = AutoMineConfig.avoidLava;
        int scanRange = AutoMineConfig.scanRange;
    }
}
