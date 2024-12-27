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
package wtf.moonlight.gui.click;

import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

@Getter
@Setter
public class Component implements IComponent {

    private float x, y, width, height;
    private Color color = INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get();
    private int colorRGB = color.getRGB();

    public void drawBackground(Color color) {
        RenderUtils.drawRect(x, y, width, height, color.getRGB());
    }
    public void drawRoundBackground(Color color) {
        RoundedUtils.drawRound(x, y, width, height,3, color);
    }
    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean isHovered(float mouseX, float mouseY, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean isVisible() {
        return true;
    }
}
