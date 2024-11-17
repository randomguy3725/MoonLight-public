package wtf.moonlight.gui.click.skeet.component.impl.sub.checkBox;

import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.click.skeet.component.ButtonComponent;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.LockedResolution;
import net.minecraft.client.gui.Gui;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

public abstract class CheckBoxComponent extends ButtonComponent implements PredicateComponent
{
    public CheckBoxComponent(final Component parent, final float x, final float y, final float width, final float height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void drawComponent(final LockedResolution resolution, final int mouseX, final int mouseY) {
        final float x = this.getX();
        final float y = this.getY();
        final float width = this.getWidth();
        final float height = this.getHeight();
        Gui.drawRect(x, y, x + width, y + height, new Color(11, 11, 11, (int) SkeetUI.getAlpha()).getRGB());
        final boolean checked = this.isChecked();
        final boolean hovered = this.isHovered(mouseX, mouseY);
        RenderUtils.drawGradientRect(x + 0.5f, y + 0.5f, width - 0.5f, height - 0.5f, false, checked ? SkeetUI.getSkeetColor() : SkeetUI.getSkeetColor(hovered ? ColorUtils.darker(4802889, 1.4f) : 4802889), checked ? ColorUtils.darker(SkeetUI.getSkeetColor(), 0.8f) : SkeetUI.getSkeetColor(hovered ? ColorUtils.darker(3158064, 1.4f) : 3158064));
    }
    
    @Override
    public void onPress(final int mouseButton) {
        if (mouseButton == 0) {
            this.setChecked(!this.isChecked());
        }
    }
    
    public abstract boolean isChecked();
    
    public abstract void setChecked(final boolean checked);
}
