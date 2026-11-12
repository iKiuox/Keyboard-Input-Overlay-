package io.kiuox.keyboardoverlay;

import net.minecraft.class_1041;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4587;

public final class KeyboardOverlayRenderer {
    private KeyboardOverlayRenderer() {}

    public static void render(class_332 context) {
        renderInternal(context, false, false);
    }

    public static void renderPreview(class_332 context) {
        renderInternal(context, true, true);
    }

    private static void renderInternal(class_332 context, boolean allowWhileScreenOpen, boolean ignoreEnabled) {
        KeyboardOverlayConfig config = KeyboardOverlayClient.CONFIG;
        if (config == null || (!ignoreEnabled && !config.enabled) || config.keys.isEmpty()) {
            return;
        }

        class_310 client = class_310.method_1551();
        if (!allowWhileScreenOpen && client.field_1755 != null) {
            return;
        }

        class_1041 window = client.method_22683();
        long handle = window.method_4490();
        class_327 textRenderer = client.field_1772;
        int x = config.x;
        int y = config.y;
        int size = config.keySize;
        int gap = config.gap;
        int border = Math.min(config.border, Math.max(0, size / 8));

        for (int code : config.keys) {
            boolean pressed = KeyboardOverlayClient.isKeyDown(handle, code);
            int background = pressed ? config.pressedColor : config.backgroundColor;
            int foreground = pressed ? config.pressedTextColor : config.textColor;

            context.method_25294(x, y, x + size, y + size, background);

            if (border > 0) {
                int borderColor = 0xFF3A3A3A;
                context.method_25294(x, y, x + size, y + border, borderColor);
                context.method_25294(x, y + size - border, x + size, y + size, borderColor);
                context.method_25294(x, y, x + border, y + size, borderColor);
                context.method_25294(x + size - border, y, x + size, y + size, borderColor);
            }

            if (!config.hideLabels) {
                String label = KeyboardOverlayClient.shortKeyName(code);
                drawFittingLabel(context, textRenderer, label, x, y, size, foreground);
            }

            x += size + gap;
        }
    }

    private static void drawFittingLabel(class_332 context, class_327 textRenderer, String label,
                                         int x, int y, int size, int color) {
        int availableWidth = Math.max(1, size - 4);
        int textWidth = textRenderer.method_1727(label);
        float scale = Math.min(1.0f, (float) availableWidth / Math.max(1, textWidth));
        scale = Math.max(0.45f, scale);

        class_4587 matrices = context.method_51448();
        matrices.method_22903();
        matrices.method_46416(x + size / 2.0f, y + size / 2.0f, 0);
        matrices.method_22905(scale, scale, 1.0f);

        int scaledWidth = Math.round(textWidth);
        int textX = -scaledWidth / 2;
        int textY = -textRenderer.field_2000 / 2;
        context.method_25303(textRenderer, label, textX, textY, color);
        matrices.method_22909();
    }
}
