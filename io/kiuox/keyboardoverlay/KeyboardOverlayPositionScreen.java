package io.kiuox.keyboardoverlay;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;

public final class KeyboardOverlayPositionScreen extends class_437 {
    private final class_437 parent;
    private boolean dragging;
    private double dragOffsetX;
    private double dragOffsetY;

    public KeyboardOverlayPositionScreen(class_437 parent) {
        super(class_2561.method_43470("Move Keyboard Overlay"));
        this.parent = parent;
    }

    @Override
    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        method_25420(context, mouseX, mouseY, delta);

        KeyboardOverlayRenderer.renderPreview(context);
        context.method_25300(field_22793,
                "Drag the overlay with left mouse • Esc / Done to save",
                field_22789 / 2, 18, 0xFFFFFFFF);
        context.method_25300(field_22793,
                "Position: " + KeyboardOverlayClient.CONFIG.x + ", " + KeyboardOverlayClient.CONFIG.y,
                field_22789 / 2, field_22790 - 28, 0xFFAAAAAA);
    }

    @Override
    public boolean method_25402(double mouseX, double mouseY, int button) {
        if (button != 0 || !insideOverlay(mouseX, mouseY)) {
            return super.method_25402(mouseX, mouseY, button);
        }
        dragging = true;
        dragOffsetX = mouseX - KeyboardOverlayClient.CONFIG.x;
        dragOffsetY = mouseY - KeyboardOverlayClient.CONFIG.y;
        return true;
    }

    @Override
    public boolean method_25403(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!dragging || button != 0) {
            return super.method_25403(mouseX, mouseY, button, deltaX, deltaY);
        }
        KeyboardOverlayClient.CONFIG.x = Math.max(0, (int) Math.round(mouseX - dragOffsetX));
        KeyboardOverlayClient.CONFIG.y = Math.max(0, (int) Math.round(mouseY - dragOffsetY));
        return true;
    }

    @Override
    public boolean method_25406(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) {
            dragging = false;
            KeyboardOverlayClient.CONFIG.save();
            return true;
        }
        return super.method_25406(mouseX, mouseY, button);
    }

    private boolean insideOverlay(double mouseX, double mouseY) {
        KeyboardOverlayConfig config = KeyboardOverlayClient.CONFIG;
        if (config.keys.isEmpty()) {
            return false;
        }
        int totalWidth = config.keys.size() * config.keySize + Math.max(0, config.keys.size() - 1) * config.gap;
        return mouseX >= config.x && mouseX <= config.x + totalWidth
                && mouseY >= config.y && mouseY <= config.y + config.keySize;
    }

    @Override
    public boolean method_25404(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            method_25419();
            return true;
        }
        return super.method_25404(keyCode, scanCode, modifiers);
    }

    @Override
    public void method_25419() {
        KeyboardOverlayClient.CONFIG.save();
        class_310.method_1551().method_1507(parent);
    }
}
