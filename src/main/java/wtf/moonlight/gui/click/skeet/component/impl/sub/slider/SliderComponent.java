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
package wtf.moonlight.gui.click.skeet.component.impl.sub.slider;

import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.click.skeet.component.ButtonComponent;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.LockedResolution;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public abstract class SliderComponent extends ButtonComponent implements PredicateComponent
{
    private boolean sliding;
    
    public SliderComponent(final Component parent, final float x, final float y, final float width, final float height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void drawComponent(final LockedResolution resolution, final int mouseX, final int mouseY) {
        final float x = this.getX();
        final float y = this.getY();
        final float width = this.getWidth();
        final float height = this.getHeight();
        final float min = this.getMin();
        final float max = this.getMax();
        final float dValue = this.getValue();
        double value;
        value = dValue;
        final boolean hovered = this.isHovered(mouseX, mouseY);
        if (this.sliding) {
            if (mouseX >= x - 0.5f && mouseY >= y - 0.5f && mouseX <= x + width + 0.5f && mouseY <= y + height + 0.5f) {
                this.setValue(MathHelper.clamp_float(this.roundToIncrement((mouseX - x) * (max - min) / (width - 1.0f) + min), min, max));
            }
            else {
                this.sliding = false;
            }
        }
        final double sliderPercentage = (value - min) / (max - min);
        final DecimalFormat format = new DecimalFormat("####.###");
        String valueString = format.format(value);

        Gui.drawRect(x, y, x + width, y + height, SkeetUI.getSkeetColor(855309));
        RenderUtils.drawGradientRect(x + 0.5f, y + 0.5f, width - 0.5f, height - 0.5f, false, SkeetUI.getSkeetColor(hovered ? ColorUtils.darker(4802889, 1.4f) : 4802889), SkeetUI.getSkeetColor(hovered ? ColorUtils.darker(3158064, 1.4f) : 3158064));
        RenderUtils.drawGradientRect(x + 0.5f, y + 0.5f, width * sliderPercentage - 0.5, height - 0.5f, false, SkeetUI.getSkeetColor(), ColorUtils.darker(SkeetUI.getSkeetColor(), 0.8f));
        Gui.drawRect(x - 2.0f, y + 1.5f, x - 0.5f, y + height - 1.5f, new Color(120, 120, 120, (int)SkeetUI.getAlpha()).getRGB());
        Gui.drawRect(x + width + 0.5f, y + 1.5f, x + width + 2.0f, y + height - 1.5f, new Color(120, 120, 120, (int)SkeetUI.getAlpha()).getRGB());
        Gui.drawRect(x + width + 1.0f, y + 1.0f, x + width + 1.5f, y + height - 1.0f, new Color(120, 120, 120, (int)SkeetUI.getAlpha()).getRGB());
        if (SkeetUI.shouldRenderText()) {
            GL11.glTranslatef(0.0f, 0.0f, 1.0f);
            if (SkeetUI.getAlpha() > 120.0) {
                SkeetUI.GROUP_BOX_HEADER_RENDERER.drawOutlinedString(valueString.replaceAll(",", "."), x + width - SkeetUI.GROUP_BOX_HEADER_RENDERER.getStringWidth(valueString), y + height / 2.0f - 7.0f, new Color(220, 220, 220, (int)SkeetUI.getAlpha()).getRGB(), new Color(0, 0, 0).getRGB());
            }
            else {
                SkeetUI.GROUP_BOX_HEADER_RENDERER.drawStringWithShadow(valueString.replaceAll(",", "."), x + width - SkeetUI.GROUP_BOX_HEADER_RENDERER.getStringWidth(valueString), y + height / 2.0f - 7.0f, new Color(230, 230, 230, (int)SkeetUI.getAlpha()).getRGB());
            }
            GL11.glTranslatef(0.0f, 0.0f, -1.0f);
        }
    }
    
    @Override
    public void onPress(final int mouseButton) {
        if (!this.sliding && mouseButton == 0) {
            this.sliding = true;
        }
    }
    
    @Override
    public void onMouseRelease(final int button) {
        this.sliding = false;
    }
    
    private float roundToIncrement(final double value) {
        final double inc = this.getIncrement();
        final double halfOfInc = inc / 2.0;
        final double floored = StrictMath.floor(value / inc) * inc;
        if (value >= floored + halfOfInc) {
            return new BigDecimal(StrictMath.ceil(value / inc) * inc).setScale(2, 4).floatValue();
        }
        return new BigDecimal(floored).setScale(2, 4).floatValue();
    }
    
    public abstract float getValue();
    
    public abstract void setValue(final float p0);
    public abstract float getMin();
    
    public abstract float getMax();
    
    public abstract float getIncrement();
}
