package wtf.moonlight.gui.click.dropdown.component.impl;

import net.minecraft.util.MathHelper;
import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class SliderComponent extends Component {

    private final SliderValue setting;
    private float anim;
    private boolean dragging;

    public SliderComponent(SliderValue setting) {
        this.setting = setting;
        setHeight(Fonts.interRegular.get(15).getHeight() * 2 + Fonts.interRegular.get(15).getHeight() + 2);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Fonts.interRegular.get(15).drawString(setting.getName(), getX() + 4, getY(), -1);

        anim = RenderUtils.animate(anim, (getWidth() - 8) * (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin()), 15);
        float sliderWidth = anim;

        RoundedUtils.drawRound(getX() + 4, getY() + Fonts.interRegular.get(15).getHeight() + 2, getWidth() - 8, 2, 2, INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get().darker().darker().darker());
        RoundedUtils.drawGradientHorizontal(getX() + 4, getY() + Fonts.interRegular.get(15).getHeight() + 2, sliderWidth, 2, 2, INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get(), INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get().darker().darker());
        RenderUtils.drawCircle(getX() + 4 + sliderWidth, getY() + Fonts.interRegular.get(15).getHeight() + 3, 0, 360, 2, 0.1f, false, -1);

        Fonts.interRegular.get(15).drawString(setting.getMin() + "", getX() + 2, getY() + Fonts.interRegular.get(15).getHeight() * 2 + 2, new Color(160, 160, 160).getRGB());
        Fonts.interRegular.get(15).drawCenteredString(setting.get() + "", getX() + getWidth() / 2, getY() + Fonts.interRegular.get(15).getHeight() * 2 + 2, -1);
        Fonts.interRegular.get(15).drawString(setting.getMax() + "", getX() - 2 + getWidth() - Fonts.interRegular.get(15).getStringWidth(setting.getMax() + ""), getY() + Fonts.interRegular.get(15).getHeight() * 2 + 2, new Color(160, 160, 160).getRGB());

        if (dragging) {
            final double difference = setting.getMax() - setting.getMin(), value = setting.getMin() + MathHelper.clamp_float((mouseX - getX()) / getWidth(), 0, 1) * difference;
            setting.setValue((float) MathUtils.incValue(value, setting.getIncrement()));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && MouseUtils.isHovered2(getX() + 2, getY() + Fonts.interRegular.get(15).getHeight() + 2, getWidth(), 2, mouseX, mouseY))
            dragging = true;
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) dragging = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean isVisible() {
        return this.setting.canDisplay();
    }
}
