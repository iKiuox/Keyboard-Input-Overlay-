package io.kiuox.keyboardoverlay;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;

/**
 * Compact, reliable configuration UI.
 * Quick Add intentionally contains only 1-9, A-Z, and left/right Shift/Ctrl.
 * Any other keyboard key can be added through the Add a key capture button.
 */
public final class KeyboardOverlayConfigScreen extends class_437 {
    private final class_437 parent;
    private boolean addingKey;
    private boolean changingToggle;
    private boolean changingConfig;

    private static final int[] DIGITS = {
            GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_3, GLFW.GLFW_KEY_4, GLFW.GLFW_KEY_5,
            GLFW.GLFW_KEY_6, GLFW.GLFW_KEY_7, GLFW.GLFW_KEY_8, GLFW.GLFW_KEY_9
    };

    private static final String[] LETTER_ROWS = {
            "QWERTYUIOP",
            "ASDFGHJKL",
            "ZXCVBNM"
    };

    private static final int[] MODIFIERS = {
            GLFW.GLFW_KEY_LEFT_SHIFT,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            GLFW.GLFW_KEY_RIGHT_CONTROL
    };

    public KeyboardOverlayConfigScreen(class_437 parent) {
        super(class_2561.method_43470("Keyboard Input Overlay"));
        this.parent = parent;
    }

    @Override
    protected void method_25426() {
        method_37067();

        final int margin = Math.max(12, Math.min(28, field_22789 / 24));
        final int center = field_22789 / 2;
        final int gap = 6;

        // Computed once up front: createQuickButtons()/createSelectedButtons() both
        // need to know where the footer sits so they can guarantee they never grow
        // into it, on any screen height.
        footerTop = field_22790 - 30;

        // Header/control row. On narrow screens controls wrap into two rows.
        int buttonW = Math.max(76, Math.min(104, (field_22789 - margin * 2 - gap * 5) / 6));
        int x = margin;
        int y = 46;
        method_37063(class_4185.method_46430(
                class_2561.method_43470(KeyboardOverlayClient.CONFIG.enabled ? "Overlay: ON" : "Overlay: OFF"),
                b -> {
                    KeyboardOverlayClient.CONFIG.enabled = !KeyboardOverlayClient.CONFIG.enabled;
                    KeyboardOverlayClient.CONFIG.save();
                    b.method_25355(class_2561.method_43470(KeyboardOverlayClient.CONFIG.enabled ? "Overlay: ON" : "Overlay: OFF"));
                }).method_46434(x, y, buttonW, 20).method_46431());
        x += buttonW + gap;

        int moveW = Math.max(60, Math.min(78, buttonW));
        method_37063(class_4185.method_46430(class_2561.method_43470("Move"),
                b -> class_310.method_1551().method_1507(new KeyboardOverlayPositionScreen(this)))
                .method_46434(x, y, moveW, 20).method_46431());
        x += moveW + gap;

        method_37063(class_4185.method_46430(class_2561.method_43470("Size -"), b -> changeSize(-2))
                .method_46434(x, y, moveW, 20).method_46431());
        x += moveW + gap;
        method_37063(class_4185.method_46430(class_2561.method_43470("Size +"), b -> changeSize(2))
                .method_46434(x, y, moveW, 20).method_46431());
        x += moveW + gap;
        method_37063(class_4185.method_46430(class_2561.method_43470("Gap -"), b -> changeGap(-1))
                .method_46434(x, y, moveW, 20).method_46431());
        x += moveW + gap;
        method_37063(class_4185.method_46430(class_2561.method_43470("Gap +"), b -> changeGap(1))
                .method_46434(x, y, moveW, 20).method_46431());

        // Labels toggle sits on its own row for narrow screens.
        int labelsY = 70;
        method_37063(class_4185.method_46430(
                class_2561.method_43470(KeyboardOverlayClient.CONFIG.hideLabels ? "Labels: OFF" : "Labels: ON"),
                b -> {
                    KeyboardOverlayClient.CONFIG.hideLabels = !KeyboardOverlayClient.CONFIG.hideLabels;
                    KeyboardOverlayClient.CONFIG.save();
                    b.method_25355(class_2561.method_43470(KeyboardOverlayClient.CONFIG.hideLabels ? "Labels: OFF" : "Labels: ON"));
                }).method_46434(margin, labelsY, 110, 20).method_46431());

        method_37063(class_4185.method_46430(
                class_2561.method_43470("Toggle key: " + KeyboardOverlayClient.keyName(KeyboardOverlayClient.CONFIG.toggleKey)),
                b -> {
                    changingToggle = true;
                    changingConfig = false;
                    addingKey = false;
                }).method_46434(margin + 116, labelsY, Math.max(150, (field_22789 - margin * 2 - 124) / 2), 20).method_46431());

        int toggleWidth = Math.max(150, (field_22789 - margin * 2 - 124) / 2);
        method_37063(class_4185.method_46430(
                class_2561.method_43470("Config key: " + KeyboardOverlayClient.keyName(KeyboardOverlayClient.CONFIG.configKey)),
                b -> {
                    changingConfig = true;
                    changingToggle = false;
                    addingKey = false;
                }).method_46434(margin + 122 + toggleWidth, labelsY, toggleWidth, 20).method_46431());

        method_37063(class_4185.method_46430(class_2561.method_43470("+  Add a key"),
                b -> {
                    addingKey = true;
                    changingToggle = false;
                    changingConfig = false;
                }).method_46434(center - 120, 96, 240, 24).method_46431());

        // Reliable clickable quick-add buttons. They are real widgets, not just painted hitboxes.
        createQuickButtons();

        // Selected key chips, each clickable to remove that exact key.
        createSelectedButtons();

        method_37063(class_4185.method_46430(class_2561.method_43470("Clear selected"), b -> {
            KeyboardOverlayClient.CONFIG.keys.clear();
            KeyboardOverlayClient.CONFIG.save();
            rebuild();
        }).method_46434(margin, footerTop, 120, 20).method_46431());

        method_37063(class_4185.method_46430(class_2561.method_43470("Reset size / gap"), b -> {
            KeyboardOverlayClient.CONFIG.keySize = 28;
            KeyboardOverlayClient.CONFIG.gap = 3;
            KeyboardOverlayClient.CONFIG.save();
            rebuild();
        }).method_46434(margin + 126, footerTop, 125, 20).method_46431());

        method_37063(class_4185.method_46430(class_2561.method_43470("Done"), b -> method_25419())
                .method_46434(field_22789 - margin - 80, footerTop, 80, 20).method_46431());

        makeWidgetsFullyVisible();
    }

    private static final int GRID_TOP = 142;
    private static final int IDEAL_KEY_H = 22;
    private static final int IDEAL_ROW_GAP = 5;
    private static final int IDEAL_GRID_ROWS = 5; // digits row + 3 letter rows + modifiers row
    private static final int IDEAL_GRID_HEIGHT = IDEAL_GRID_ROWS * IDEAL_KEY_H + (IDEAL_GRID_ROWS - 1) * IDEAL_ROW_GAP;
    // Minimum room the SELECTED section needs below the grid: its title line plus
    // one full row of chips, with a little padding on each side.
    private static final int MIN_SELECTED_BLOCK = 50;

    private void createQuickButtons() {
        int maxWidth = Math.max(180, field_22789 - 24);
        int gap = 4;
        int keyW = Math.max(18, Math.min(46, (maxWidth - 9 * gap) / 10));
        int center = field_22789 / 2;

        // Adaptive vertical scale: on a screen tall enough for the full-size grid
        // (22px rows, 5px gaps) PLUS the SELECTED title and at least one chip row
        // above the footer, this always resolves to 1.0 — i.e. pixel-for-pixel the
        // same layout as before, nothing changes on screens that already fit fine.
        // Only on a screen too short for that (different launcher/resolution/GUI
        // scale) do the rows shrink, just enough to guarantee the grid and the
        // SELECTED section below it can never overlap, on any resolution.
        int availableForGrid = footerTop - 8 - MIN_SELECTED_BLOCK - GRID_TOP;
        float scale = availableForGrid >= IDEAL_GRID_HEIGHT
                ? 1.0f
                : Math.max(0.35f, availableForGrid / (float) IDEAL_GRID_HEIGHT);
        gridScale = scale;

        int keyH = Math.max(10, Math.round(IDEAL_KEY_H * scale));
        int rowGap = Math.max(2, Math.round(IDEAL_ROW_GAP * scale));
        int y = GRID_TOP;

        int digitWidth = 9 * keyW + 8 * gap;
        int x = center - digitWidth / 2;
        for (int code : DIGITS) {
            addQuickButton(code, x, y, keyW, keyH);
            x += keyW + gap;
        }
        y += keyH + rowGap;

        for (String row : LETTER_ROWS) {
            List<Integer> codes = new ArrayList<>();
            for (int i = 0; i < row.length(); i++) codes.add(letterCode(row.charAt(i)));
            int rowWidth = codes.size() * keyW + (codes.size() - 1) * gap;
            x = center - rowWidth / 2;
            for (int code : codes) {
                addQuickButton(code, x, y, keyW, keyH);
                x += keyW + gap;
            }
            y += keyH + rowGap;
        }

        int modifierW = Math.min(86, Math.max(58, keyW + 32));
        int modifierGap = Math.max(3, Math.round(5 * scale));
        int modifierWidth = MODIFIERS.length * modifierW + 3 * modifierGap;
        x = center - modifierWidth / 2;
        for (int code : MODIFIERS) {
            addQuickButton(code, x, y, modifierW, keyH);
            x += modifierW + modifierGap;
        }

        // Where the grid actually ends on THIS screen (after any scaling above),
        // so the SELECTED section below is always placed relative to the grid's
        // real bottom edge, never a distance guessed from the screen height.
        quickAddBottom = y + keyH;
    }

    private void addQuickButton(int code, int x, int y, int w, int h) {
        boolean selected = KeyboardOverlayClient.CONFIG.keys.contains(code);
        String prefix = selected ? "✓ " : "";
        method_37063(class_4185.method_46430(class_2561.method_43470(prefix + KeyboardOverlayClient.keyName(code)), b -> toggleKey(code))
                .method_46434(x, y, w, h).method_46431());
    }

    private void createSelectedButtons() {
        // Same idea as the grid: on a screen with enough room this is exactly the
        // old fixed 14px gap. On a short screen, the grid above has already shrunk
        // to leave MIN_SELECTED_BLOCK of guaranteed clear space here, so this can
        // never land on top of the grid, and the chip loop below can never land on
        // top of the footer — on any resolution or GUI scale.
        int titleGap = Math.max(6, Math.round(14 * gridScale));
        int titleY = quickAddBottom + titleGap;
        contextlessSelectedTitleY = titleY;
        int x = 12;
        int y = titleY + 18;
        int maxX = field_22789 - 12;

        for (Integer code : new ArrayList<>(KeyboardOverlayClient.CONFIG.keys)) {
            String label = KeyboardOverlayClient.keyName(code) + "  ×";
            int w = Math.max(42, Math.min(110, 16 + class_310.method_1551().field_1772.method_1727(label)));
            if (x + w > maxX) {
                x = 12;
                y += 24;
            }
            // Keep chips above the footer; when there are many selected keys, the footer area remains safe.
            if (y + 20 > footerTop - 6) break;
            method_37063(class_4185.method_46430(class_2561.method_43470(label), b -> {
                KeyboardOverlayClient.removeKey(code);
                rebuild();
            }).method_46434(x, y, w, 20).method_46431());
            x += w + 4;
        }
    }

    private int contextlessSelectedTitleY;
    private int quickAddBottom;
    private int footerTop;
    private float gridScale = 1.0f;

    private void rebuild() {
        method_37067();
        method_25426();
    }

    private void makeWidgetsFullyVisible() {
        for (var child : method_25396()) {
            if (child instanceof class_4185 button) {
                button.field_22764 = true;
                button.field_22763 = true;
                button.method_25350(1.0f);
            }
        }
    }


    private void changeSize(int amount) {
        KeyboardOverlayConfig c = KeyboardOverlayClient.CONFIG;
        c.keySize = Math.max(12, Math.min(96, c.keySize + amount));
        c.save();
        rebuild();
    }

    private void changeGap(int amount) {
        KeyboardOverlayConfig c = KeyboardOverlayClient.CONFIG;
        c.gap = Math.max(0, Math.min(24, c.gap + amount));
        c.save();
        rebuild();
    }

    @Override
    public boolean method_25404(int keyCode, int scanCode, int modifiers) {
        if (addingKey || changingToggle || changingConfig) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                addingKey = false;
                changingToggle = false;
                changingConfig = false;
                return true;
            }
            if (!KeyboardOverlayConfig.isSupportedKeyboardCode(keyCode)) return true;

            if (addingKey) {
                // Capture works as a toggle: pressing an already selected key removes it; otherwise adds it.
                if (KeyboardOverlayClient.CONFIG.keys.contains(keyCode)) {
                    KeyboardOverlayClient.removeKey(keyCode);
                } else {
                    KeyboardOverlayClient.addKey(keyCode);
                }
            } else if (changingToggle) {
                KeyboardOverlayClient.setToggleKey(keyCode);
            } else {
                KeyboardOverlayClient.setConfigKey(keyCode);
            }

            addingKey = false;
            changingToggle = false;
            changingConfig = false;
            rebuild();
            return true;
        }
        return super.method_25404(keyCode, scanCode, modifiers);
    }

    @Override
    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        boolean modal = addingKey || changingToggle || changingConfig;

        // Root cause of the old bleed-through: button LABEL TEXT in this Minecraft
        // version is drawn through a buffered text layer that can flush to the screen
        // *after* fill()/rect calls that appear later in the code, so painting a dark
        // rectangle "over" the buttons was never reliable — the text could still pop
        // on top of it. The only fully reliable fix is to not draw the buttons at all
        // while the capture box is open. Setting visible = false skips their render
        // entirely (background AND text), so nothing can bleed through the panel.
        for (var child : method_25396()) {
            if (child instanceof class_4185 button) {
                button.field_22764 = !modal;
            }
        }

        // Draw the screen background first. Widgets and labels are rendered next so they
        // are never placed underneath a dark modal overlay.
        method_25420(context, mouseX, mouseY, delta);

        super.method_25394(context, mouseX, mouseY, delta);

        int center = field_22789 / 2;
        context.method_25300(field_22793, "Keyboard Input Overlay", center, 12, 0xFFFFFFFF);
        context.method_25300(field_22793,
                "Quick Add: 1-9 • A-Z • Shift/Ctrl  |  Add a key for anything else",
                center, 26, 0xFFDDDDDD);
        context.method_25303(field_22793,
                "Size: " + KeyboardOverlayClient.CONFIG.keySize + "    Gap: " + KeyboardOverlayClient.CONFIG.gap,
                12, 32, 0xFFFFFFFF);

        // These section headers sit right above the quick-add grid / selected chips,
        // so hide them too while the modal is open — with the buttons gone there is
        // nothing left for them to label, and it keeps the dimmed screen clean.
        if (!modal) {
            context.method_25303(field_22793, "QUICK ADD", 12, 126, 0xFFFFFFFF);
            context.method_25303(field_22793,
                    "SELECTED  •  click a selected key to remove it",
                    12, contextlessSelectedTitleY, 0xFFFFFFFF);
        }

        // Modal mode: dim the underlying UI, then place the capture message in a dedicated
        // translucent panel. Every button above is already invisible, so this dim layer
        // and the panel are the only things left to draw — nothing behind them can show
        // through anymore.
        if (modal) {
            context.method_25294(0, 0, field_22789, field_22790, 0xB0000000);

            String title = addingKey ? "ADD A KEY" : (changingToggle ? "SET TOGGLE KEY" : "SET CONFIG KEY");
            String instruction = addingKey ? "Press any Keyboard Key" : "Press the key you want to use";
            String cancel = "ESC  Cancel";

            int panelWidth = Math.min(460, Math.max(280, field_22789 - 40));
            int panelHeight = 130;
            int panelLeft = center - panelWidth / 2;
            int panelTop = field_22790 / 2 - panelHeight / 2;

            // Soft outer shadow + dark opaque panel + thin highlight border.
            context.method_25294(panelLeft + 2, panelTop + 2, panelLeft + panelWidth + 2, panelTop + panelHeight + 2, 0x60000000);
            context.method_25294(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, 0xFF181818);
            context.method_25294(panelLeft, panelTop, panelLeft + panelWidth, panelTop + 1, 0xFFD8D8D8);
            context.method_25294(panelLeft, panelTop + panelHeight - 1, panelLeft + panelWidth, panelTop + panelHeight, 0xFF555555);
            context.method_25294(panelLeft, panelTop, panelLeft + 1, panelTop + panelHeight, 0xFFD8D8D8);
            context.method_25294(panelLeft + panelWidth - 1, panelTop, panelLeft + panelWidth, panelTop + panelHeight, 0xFF555555);

            context.method_25300(field_22793, title, center, panelTop + 22, 0xFFFFFFFF);
            context.method_25300(field_22793, instruction, center, panelTop + 56, 0xFFE8E8E8);
            context.method_25300(field_22793, cancel, center, panelTop + 92, 0xFFBDBDBD);
        }
    }

    private void toggleKey(int code) {
        if (KeyboardOverlayClient.CONFIG.keys.contains(code)) {
            KeyboardOverlayClient.removeKey(code);
        } else {
            KeyboardOverlayClient.addKey(code);
        }
        rebuild();
    }

    private static int letterCode(char c) {
        return GLFW.GLFW_KEY_A + (Character.toUpperCase(c) - 'A');
    }

    @Override
    public void method_25419() {
        KeyboardOverlayClient.CONFIG.save();
        class_310.method_1551().method_1507(parent);
    }
}
