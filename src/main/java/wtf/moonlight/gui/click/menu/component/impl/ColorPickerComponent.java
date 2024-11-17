package wtf.moonlight.gui.click.menu.component.impl;

import net.minecraft.util.MathHelper;
import wtf.moonlight.features.values.impl.ColorValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

public class ColorPickerComponent extends Component {

    private final ColorValue setting;
    private boolean draggingHue;
    private boolean draggingSaturation;
    private boolean draggingBrightness;
    private float selectedXHue;
    private float selectedXSaturation;
    private float selectedXBrightness;

    public ColorPickerComponent(ColorValue setting) {
        this.setting = setting;
        setHeight(30);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        final float[] hsb = new float[]{setting.getHue(), setting.getSaturation(), setting.getBrightness()};

        if (draggingHue) {
            selectedXHue = MathHelper.clamp_float(mouseX - getX() - 70, 0, 70);
            setting.setHue(selectedXHue / 70);
        }

        if (draggingSaturation) {
            selectedXSaturation = MathHelper.clamp_float(mouseX - getX() - 70, 0, 70);
            setting.setSaturation(selectedXSaturation / 70);
        }

        if (draggingBrightness) {
            selectedXBrightness = MathHelper.clamp_float(mouseX - getX() - 70, 0, 70);
            setting.setBrightness(selectedXBrightness / 70);
        }

        Fonts.interMedium.get(17).drawString(setting.getName(), getX() + 60 - Fonts.interMedium.get(17).getStringWidth(setting.getName()), getY() + 7, -1);

        for (int max = 70, i = 0; i < max; i++) {

            RenderUtils.drawRect(getX() + i + 70, getY() + 7, 1, 7, Color.getHSBColor(i / (float) max, hsb[1], hsb[2]).getRGB());
            RenderUtils.drawRect(getX() + 70 + selectedXHue, getY() + 7, 1, 7, -1);

            RenderUtils.drawRect(getX() + 70 + i, getY() + 17, 1, 7, Color.getHSBColor(hsb[0], i / (float) max, hsb[2]).getRGB());
            RenderUtils.drawRect(getX() + 70 + selectedXSaturation, getY() + 17, 1, 7, -1);

            RenderUtils.drawRect(getX() + i + 70, getY() + 27, 1, 7, Color.getHSBColor(hsb[0], hsb[1], i / (float) max).getRGB());
            RenderUtils.drawRect(getX() + 70 + selectedXBrightness, getY() + 27, 1, 7, -1);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(getX() + 70, getY() + 7, 70, 7, mouseX, mouseY) && mouseButton == 0) {
            draggingHue = true;
        }
        if (MouseUtils.isHovered2(getX() + 70, getY() + 17, 70, 7, mouseX, mouseY) && mouseButton == 0) {
            draggingSaturation = true;
        }
        if (MouseUtils.isHovered2(getX() + 70, getY() + 27, 70, 7, mouseX, mouseY) && mouseButton == 0) {
            draggingBrightness = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            draggingHue = false;
            draggingSaturation = false;
            draggingBrightness = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean isVisible() {
        return setting.canDisplay();
    }
}
