package io.kiuox.keyboardoverlay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class KeyboardOverlayClient implements ClientModInitializer {
    public static final String MOD_ID = "keyboardoverlay";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static KeyboardOverlayConfig CONFIG;

    private static class_304 toggleKeyBinding;
    private static class_304 configKeyBinding;
    public static final class_2960 HUD_LAYER = class_2960.method_60655(MOD_ID, "keyboard_overlay");

    private static final Map<Integer, String> KEY_LABELS = createKeyLabels();
    private static final Map<Integer, String> SHORT_LABELS = createShortLabels();

    @Override
    public void onInitializeClient() {
        CONFIG = KeyboardOverlayConfig.load();

        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new class_304(
                "key.keyboardoverlay.toggle", class_3675.class_307.field_1668, CONFIG.toggleKey, "category.keyboardoverlay"));
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new class_304(
                "key.keyboardoverlay.config", class_3675.class_307.field_1668, CONFIG.configKey, "category.keyboardoverlay"));

        // The normal HUD layer is only drawn while Minecraft is in gameplay.
        // No ScreenEvents are registered, so the overlay does not appear over menus/inventories.
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerAfter(
                IdentifiedLayer.MISC_OVERLAYS,
                HUD_LAYER,
                (context, tickCounter) -> KeyboardOverlayRenderer.render(context)
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKeyBinding.method_1436()) {
                CONFIG.enabled = !CONFIG.enabled;
                CONFIG.save();
            }
            while (configKeyBinding.method_1436()) {
                client.method_1507(new KeyboardOverlayConfigScreen(client.field_1755));
            }
        });
    }

    public static boolean isKeyDown(long windowHandle, int glfwKeyCode) {
        return class_3675.method_15987(windowHandle, glfwKeyCode);
    }

    public static class_3675.class_306 keyFromCode(int code) {
        return class_3675.class_307.field_1668.method_1447(code);
    }

    public static String keyName(int code) {
        String label = KEY_LABELS.get(code);
        if (label != null) return label;

        String glfwName = GLFW.glfwGetKeyName(code, 0);
        if (glfwName != null && !glfwName.isBlank()) return glfwName.toUpperCase();
        return "KEY " + code;
    }

    /**
     * Short label used only by the in-game HUD boxes, where a long name like
     * "L SHIFT" no longer fits cleanly once the box is shrunk down.
     * The config screen (Quick Add buttons, chips, "Toggle key: ..." text) keeps
     * using the full keyName() — only the on-screen overlay boxes use this.
     */
    public static String shortKeyName(int code) {
        String label = SHORT_LABELS.get(code);
        if (label != null) return label;
        return keyName(code);
    }

    public static List<Integer> keyboardKeyCodes() {
        return new ArrayList<>(KEY_LABELS.keySet());
    }

    public static boolean isKnownQuickKey(int code) {
        return KEY_LABELS.containsKey(code);
    }

    public static void addKey(int keyCode) {
        if (!CONFIG.keys.contains(keyCode)) {
            CONFIG.keys.add(keyCode);
            CONFIG.save();
        }
    }

    public static void removeKey(int keyCode) {
        CONFIG.keys.remove((Integer) keyCode);
        CONFIG.save();
    }

    public static void setToggleKey(int keyCode) {
        if (toggleKeyBinding != null) {
            toggleKeyBinding.method_1422(class_3675.class_307.field_1668.method_1447(keyCode));
            class_304.method_1426();
            CONFIG.toggleKey = keyCode;
            CONFIG.save();
        }
    }

    public static void setConfigKey(int keyCode) {
        if (configKeyBinding != null) {
            configKeyBinding.method_1422(class_3675.class_307.field_1668.method_1447(keyCode));
            class_304.method_1426();
            CONFIG.configKey = keyCode;
            CONFIG.save();
        }
    }

    private static Map<Integer, String> createKeyLabels() {
        Map<Integer, String> labels = new LinkedHashMap<>();

        // Main number row.
        add(labels, GLFW.GLFW_KEY_1, "1"); add(labels, GLFW.GLFW_KEY_2, "2");
        add(labels, GLFW.GLFW_KEY_3, "3"); add(labels, GLFW.GLFW_KEY_4, "4");
        add(labels, GLFW.GLFW_KEY_5, "5"); add(labels, GLFW.GLFW_KEY_6, "6");
        add(labels, GLFW.GLFW_KEY_7, "7"); add(labels, GLFW.GLFW_KEY_8, "8");
        add(labels, GLFW.GLFW_KEY_9, "9"); add(labels, GLFW.GLFW_KEY_0, "0");

        // Alphabet.
        for (int i = GLFW.GLFW_KEY_A; i <= GLFW.GLFW_KEY_Z; i++) {
            add(labels, i, Character.toString((char) ('A' + i - GLFW.GLFW_KEY_A)));
        }

        add(labels, GLFW.GLFW_KEY_ESCAPE, "ESC");
        add(labels, GLFW.GLFW_KEY_TAB, "TAB");
        add(labels, GLFW.GLFW_KEY_CAPS_LOCK, "CAPS");
        add(labels, GLFW.GLFW_KEY_LEFT_SHIFT, "L SHIFT");
        add(labels, GLFW.GLFW_KEY_RIGHT_SHIFT, "R SHIFT");
        add(labels, GLFW.GLFW_KEY_LEFT_CONTROL, "L CTRL");
        add(labels, GLFW.GLFW_KEY_RIGHT_CONTROL, "R CTRL");
        add(labels, GLFW.GLFW_KEY_LEFT_ALT, "L ALT");
        add(labels, GLFW.GLFW_KEY_RIGHT_ALT, "R ALT");
        add(labels, GLFW.GLFW_KEY_SPACE, "SPACE");
        add(labels, GLFW.GLFW_KEY_ENTER, "ENTER");
        add(labels, GLFW.GLFW_KEY_BACKSPACE, "BACKSPACE");
        add(labels, GLFW.GLFW_KEY_INSERT, "INSERT");
        add(labels, GLFW.GLFW_KEY_DELETE, "DELETE");
        add(labels, GLFW.GLFW_KEY_HOME, "HOME");
        add(labels, GLFW.GLFW_KEY_END, "END");
        add(labels, GLFW.GLFW_KEY_PAGE_UP, "PAGE UP");
        add(labels, GLFW.GLFW_KEY_PAGE_DOWN, "PAGE DOWN");
        add(labels, GLFW.GLFW_KEY_UP, "UP");
        add(labels, GLFW.GLFW_KEY_DOWN, "DOWN");
        add(labels, GLFW.GLFW_KEY_LEFT, "LEFT");
        add(labels, GLFW.GLFW_KEY_RIGHT, "RIGHT");

        for (int i = 1; i <= 12; i++) {
            add(labels, GLFW.GLFW_KEY_F1 + (i - 1), "F" + i);
        }

        return Collections.unmodifiableMap(labels);
    }

    private static void add(Map<Integer, String> labels, int code, String label) {
        labels.put(code, label);
    }

    private static Map<Integer, String> createShortLabels() {
        Map<Integer, String> s = new LinkedHashMap<>();
        add(s, GLFW.GLFW_KEY_LEFT_SHIFT, "LS");
        add(s, GLFW.GLFW_KEY_RIGHT_SHIFT, "RS");
        add(s, GLFW.GLFW_KEY_LEFT_CONTROL, "LC");
        add(s, GLFW.GLFW_KEY_RIGHT_CONTROL, "RC");
        add(s, GLFW.GLFW_KEY_LEFT_ALT, "LA");
        add(s, GLFW.GLFW_KEY_RIGHT_ALT, "RA");
        add(s, GLFW.GLFW_KEY_SPACE, "SPC");
        add(s, GLFW.GLFW_KEY_ENTER, "ENT");
        add(s, GLFW.GLFW_KEY_BACKSPACE, "BKSP");
        add(s, GLFW.GLFW_KEY_INSERT, "INS");
        add(s, GLFW.GLFW_KEY_DELETE, "DEL");
        add(s, GLFW.GLFW_KEY_PAGE_UP, "PGUP");
        add(s, GLFW.GLFW_KEY_PAGE_DOWN, "PGDN");
        add(s, GLFW.GLFW_KEY_DOWN, "DN");
        add(s, GLFW.GLFW_KEY_LEFT, "LT");
        add(s, GLFW.GLFW_KEY_RIGHT, "RT");
        return Collections.unmodifiableMap(s);
    }
}
