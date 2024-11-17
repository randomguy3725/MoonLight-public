package wtf.moonlight.gui.click.menu.component.impl;

import net.minecraft.util.MathHelper;
import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

public class SliderComponent extends Component {
    private final SliderValue setting;
    private boolean dragging;
    private float anim;
    private final Animation drag = new DecelerateAnimation(250, 1);

    public SliderComponent(SliderValue setting) {
        this.setting = setting;
        setHeight(24);
        drag.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        Fonts.interMedium.get(13).drawString(setting.getName(), getX() + 6, getY() + 15, -1);
        RoundedUtils.drawRound(getX() + 6, getY() + 22, (float) INSTANCE.getMenuGUI().getWidth() / 2 - 40, 2, 4, INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get().darker().darker());

        anim = RenderUtils.animate(anim, (((float) INSTANCE.getMenuGUI().getWidth() / 2) - 40) * (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin()), 50);
        float sliderWidth = anim;
        drag.setDirection(dragging ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtils.drawRound(getX() + 6, getY() + 22, sliderWidth, 2, 1, INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get());
        RenderUtils.drawCircle(getX() + 6 + sliderWidth, getY() + 23, 0, 360, (float) 3, .1f, true, -1);
        if (dragging) {
            final double difference = this.setting.getMax() - this.setting
                    .getMin(), //
                    value = this.setting.getMin() + MathHelper
                            .clamp_double((mouseX - (getX() + 6)) / (((float) INSTANCE.getMenuGUI().getWidth() / 2) - 40), 0, 1) * difference;
            setting.setValue((float) MathUtils.incValue(value, setting.getIncrement()));
        }

        String newValue = String.valueOf((float) MathUtils.incValue(setting.get(), setting.getIncrement()));

        if (setting.getIncrement() == 1) {
            newValue = newValue.replace(".0", "");
        }

        Fonts.interMedium.get(13).drawStringWithShadow(newValue, getX() + ((float) INSTANCE.getMenuGUI().getWidth() / 2) - Fonts.interMedium.get(13).getStringWidth((newValue)) - 40, getY() + 15, -1);

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(getX() + 6, getY() + 22, (float) INSTANCE.getMenuGUI().getWidth() / 2, 2, mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            dragging = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean isVisible() {
        return setting.canDisplay();
    }
}
