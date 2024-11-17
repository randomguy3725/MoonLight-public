package wtf.moonlight.gui.click.dropdown2.component.impl;

import net.minecraft.util.MathHelper;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

public class SliderComponent extends Component {
    private final SliderValue setting;
    private float anim;
    private boolean dragging;
    private final float distanceToBorder = 5;
    public SliderComponent(SliderValue setting) {
        this.setting = setting;
        setHeight(Fonts.interRegular.get(14).getHeight() + 2 + 2.5f);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        anim = RenderUtils.animate(anim, (getWidth() - distanceToBorder * 2) * (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin()), 15);
        float sliderWidth = anim;

        Fonts.interRegular.get(14).drawString(setting.getName(),getX() + distanceToBorder,getY(),-1);
        Fonts.interRegular.get(14).drawString(setting.get() + "",getX() + distanceToBorder + getWidth() - distanceToBorder * 2,getY(),-1);
        RoundedUtils.drawRound(getX() + distanceToBorder,getY() + Fonts.interRegular.get(14).getHeight() + 2,sliderWidth,2.5f,3,getColor());
        if (dragging) {
            final double difference = setting.getMax() - setting.getMin(), value = setting.getMin() + MathHelper.clamp_float((mouseX - getX()) / getWidth(), 0, 1) * difference;
            setting.setValue((float) MathUtils.incValue(value, setting.getIncrement()));
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        anim = RenderUtils.animate(anim, (getWidth() - distanceToBorder * 2) * (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin()), 15);
        float sliderWidth = anim;

        if (mouseButton == 0 && MouseUtils.isHovered2(getX() + distanceToBorder,getY() + Fonts.interRegular.get(14).getHeight() + 2,sliderWidth,2.5f, mouseX, mouseY))
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

