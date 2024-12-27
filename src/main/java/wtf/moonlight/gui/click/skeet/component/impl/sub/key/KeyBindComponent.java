/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.gui.click.skeet.component.impl.sub.key;

import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.click.skeet.component.ButtonComponent;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.LockedResolution;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.gui.font.FontRenderer;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class KeyBindComponent extends ButtonComponent
{
    private static final FontRenderer FONT_RENDERER = SkeetUI.KEYBIND_FONT_RENDERER;
    public static final FontRenderer abcFont = new FontRenderer(new Font("Tahoma", Font.PLAIN, 9), false);
    private final Supplier<Integer> getBind;
    private final Consumer<Integer> onSetBind;
    private boolean binding;


    public KeyBindComponent(final Component parent, final Supplier<Integer> getBind, final Consumer<Integer> onSetBind, final float x, final float y) {
        super(parent, x, y, KeyBindComponent.FONT_RENDERER.getStringWidth("[") * 2.0f, KeyBindComponent.FONT_RENDERER.getHeight());
        this.getBind = getBind;
        this.onSetBind = onSetBind;
    }

    @Override
    public float getWidth() {
        return super.getWidth() + abcFont.getStringWidth(this.getBind());
    }

    @Override
    public void drawComponent(final LockedResolution lockedResolution, final int mouseX, final int mouseY) {
        final float x = this.getX();
        final float y = this.getY();
        final float width = this.getWidth();
        abcFont.drawString("[", x + 40.166668f - width, y, binding ? new Color(216, 56, 56).getRGB() : new Color(75, 75, 75, (int) SkeetUI.getAlpha()).getRGB());
        abcFont.drawString(this.getBind().toUpperCase() , abcFont.getStringWidth("[") + x + 40.166668f - width, y, binding ? new Color(216, 56, 56).getRGB() : new Color(75, 75, 75, (int) SkeetUI.getAlpha()).getRGB());
        abcFont.drawString("]", abcFont.getStringWidth("[") + abcFont.getStringWidth(this.getBind().toUpperCase()) + x + 40.166668f - width, y, binding ? new Color(216, 56, 56).getRGB() : new Color(75, 75, 75, (int) SkeetUI.getAlpha()).getRGB());
    }

    @Override
    public boolean isHovered(final int mouseX, final int mouseY) {
        final float x = this.getX();
        final float y = this.getY();
        return mouseX >= x + 40.166668f - this.getWidth() && mouseY >= y && mouseX <= x + 40.166668f && mouseY <= y + this.getHeight();
    }

    @Override
    public void onKeyPress(int keyCode) {
        if (this.binding) {
            if (keyCode == 211) {
                keyCode = 0;
            }
            this.onChangeBind(keyCode);
            this.binding = false;
        }
    }

    private String getBind() {
        final int bind = this.getBind.get();
        if (bind == Keyboard.KEY_RSHIFT) {
            return "-";
        }
        return this.binding ? "-" : (bind == 0 ? "-" : Keyboard.getKeyName(bind));

    }

    private void onChangeBind(final int bind) {
        this.onSetBind.accept(bind);
    }

    @Override
    public void onPress(final int mouseButton) {
        this.binding = !this.binding;
    }
}