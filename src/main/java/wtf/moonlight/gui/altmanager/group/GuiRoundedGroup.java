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
package wtf.moonlight.gui.altmanager.group;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import wtf.moonlight.gui.font.FontRenderer;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;


@Getter
public class GuiRoundedGroup extends AbstractGroup {
    protected final int radius;

    public GuiRoundedGroup(String title, int xPosition, int yPosition, int width, int height, int radius,
                           FontRenderer titleFontRenderer) {
        super(title, xPosition, yPosition, width, height, titleFontRenderer);
        this.radius = radius;
    }

    public GuiRoundedGroup(String title, int xPosition, int yPosition, int width, int height, int radius) {
        super(title, xPosition, yPosition, width, height);
        this.radius = radius;
    }

    @Override
    public void drawGroup(Minecraft mc, int mouseX, int mouseY) {
        if (this.hidden) return;

        RenderUtils.drawRoundedRect(this.xPosition,
                this.yPosition, this.width, this.height, this.radius, new Color(0, 0, 0,80).getRGB());

        if (this.title != null) {
            this.titleFontRenderer.drawString(this.title,
                    this.xPosition + (this.width - this.titleFontRenderer.getStringWidth(this.title)) / 2.0F,
                    this.yPosition + 4,
                    new Color(198, 198, 198).getRGB());
        }
    }

}
