package io.kiuox.keyboardoverlay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class KeyboardOverlayConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("keyboardoverlay.json");

    public boolean enabled = true;
    public boolean hideLabels = false;
    public int x = 10;
    public int y = 10;
    public int keySize = 28;
    public int gap = 3;
    public int border = 2;
    public int backgroundColor = 0xCC000000;
    public int pressedColor = 0xFFFFFFFF;
    public int textColor = 0xFFFFFFFF;
    public int pressedTextColor = 0xFF000000;
    public int toggleKey = GLFW.GLFW_KEY_F8;
    public int configKey = GLFW.GLFW_KEY_F7;
    public List<Integer> keys = new ArrayList<>(List.of(
            GLFW.GLFW_KEY_W,
            GLFW.GLFW_KEY_A,
            GLFW.GLFW_KEY_S,
            GLFW.GLFW_KEY_D,
            GLFW.GLFW_KEY_SPACE
    ));

    public void sanitize() {
        x = clamp(x, 0, 4096);
        y = clamp(y, 0, 4096);
        keySize = clamp(keySize, 12, 96);
        gap = clamp(gap, 0, 24);
        border = clamp(border, 0, 8);
        toggleKey = clampKey(toggleKey, GLFW.GLFW_KEY_F8);
        configKey = clampKey(configKey, GLFW.GLFW_KEY_F7);

        if (keys == null) {
            keys = new ArrayList<>();
        }

        LinkedHashSet<Integer> unique = new LinkedHashSet<>();
        for (Integer key : keys) {
            if (key != null && isSupportedKeyboardCode(key)) {
                unique.add(key);
            }
        }
        keys = new ArrayList<>(unique);
    }

    private static int clampKey(int value, int fallback) {
        return isSupportedKeyboardCode(value) ? value : fallback;
    }

    public static boolean isSupportedKeyboardCode(int code) {
        return code >= GLFW.GLFW_KEY_SPACE && code <= GLFW.GLFW_KEY_LAST;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static KeyboardOverlayConfig load() {
        if (!Files.exists(PATH)) {
            KeyboardOverlayConfig config = new KeyboardOverlayConfig();
            config.sanitize();
            config.save();
            return config;
        }

        try (Reader reader = Files.newBufferedReader(PATH)) {
            KeyboardOverlayConfig config = GSON.fromJson(reader, KeyboardOverlayConfig.class);
            if (config == null) {
                config = new KeyboardOverlayConfig();
            }
            config.sanitize();
            return config;
        } catch (Exception ignored) {
            KeyboardOverlayConfig config = new KeyboardOverlayConfig();
            config.sanitize();
            return config;
        }
    }

    public void save() {
        sanitize();
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException exception) {
            KeyboardOverlayClient.LOGGER.error("Failed to save keyboard overlay config", exception);
        }
    }
}
