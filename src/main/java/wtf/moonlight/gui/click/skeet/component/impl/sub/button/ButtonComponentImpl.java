/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.gui.click.skeet.component.impl.sub.button;

import wtf.moonlight.gui.click.skeet.LockedResolution;
import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.click.skeet.component.ButtonComponent;
import wtf.moonlight.gui.click.skeet.component.Component;
import net.minecraft.client.gui.Gui;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.function.Consumer;

public final class ButtonComponentImpl extends ButtonComponent
{
    private final String text;
    private final Consumer<Integer> onPress;
    
    public ButtonComponentImpl(final Component parent, final String text, final Consumer<Integer> onPress, final float width, final float height) {
        super(parent, 0.0f, 0.0f, width, height);
        this.text = text;
        this.onPress = onPress;
    }
    
    @Override
    public void drawComponent(final LockedResolution lockedResolution, final int mouseX, final int mouseY) {
        final float x = this.getX();
        final float y = this.getY();
        final float width = this.getWidth();
        final float height = this.getHeight();
        final boolean hovered = this.isHovered(mouseX, mouseY);
        Gui.drawRect(x, y, x + width, y + height, SkeetUI.getSkeetColor(1118481));
        Gui.drawRect(x + 0.5f, y + 0.5f, x + width - 0.5f, y + height - 0.5f, SkeetUI.getSkeetColor(2500134));
        RenderUtils.drawGradientRect(x + 1.0f, y + 1.0f, width - 1.0f, height - 1.0f, false, SkeetUI.getSkeetColor(hovered ? ColorUtils.darker(2236962, 1.2f) : 2236962), SkeetUI.getSkeetColor(hovered ? ColorUtils.darker(1973790, 1.2f) : 1973790));
        if (SkeetUI.shouldRenderText()) {
            SkeetUI.FONT_RENDERER.drawOutlinedString(this.text, x + width / 2.0f - SkeetUI.FONT_RENDERER.getStringWidth(this.text) / 2.0f, y + height / 2.0f, SkeetUI.getSkeetColor(16777215), SkeetUI.getSkeetColor(0));
        }
    }
    
    @Override
    public void onPress(final int mouseButton) {
        this.onPress.accept(mouseButton);
    }
}
