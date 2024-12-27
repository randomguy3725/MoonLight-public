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
package wtf.moonlight.gui.click.neverlose.components.settings;

import net.minecraft.util.MathHelper;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

public class SliderComponent extends Component {
    private final SliderValue setting;
    private float anim;
    private boolean dragging;
    private final Animation drag = new DecelerateAnimation(250, 1);
    public SliderComponent(SliderValue setting) {
        this.setting = setting;
        setHeight(24);
        drag.setDirection(Direction.BACKWARDS);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtils.drawRound(getX() + 4, getY() + 10, 172, .5f, 4, lineColor2);

        Fonts.interSemiBold.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, ColorUtils.interpolateColor2(Color.WHITE.darker().darker(), Color.WHITE, (float) drag.getOutput()));
        RoundedUtils.drawRound(getX() + 90, getY() + 22, 60, 2, 1, sliderBgColor);

        RoundedUtils.drawRoundOutline(getX() + 154, getY() + 18, 20, 10, 2, .1f, bgColor4, outlineColor2);

        Fonts.interSemiBold.get(12).drawCenteredString((int) ((setting).get() * 100.0D) / 100.0D + "", getX() + 164, getY() + 22, textRGB);
        anim = RenderUtils.animate(anim, 60 * (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin()), 50);
        float sliderWidth = anim;
        drag.setDirection(dragging ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtils.drawRound(getX() + 90, getY() + 22, sliderWidth, 2, 1, sliderBarColor);
        RenderUtils.drawCircle(getX() + 90 + sliderWidth, getY() + 23, 0, 360, (float) 3, .1f, true, sliderCircleColor.getRGB());
        if (dragging) {
            final double difference = this.setting.getMax() - this.setting
                    .getMin(), //
                    value = this.setting.getMin() + MathHelper
                            .clamp_double((mouseX - (getX() + 90)) / 60, 0, 1) * difference;
            setting.setValue((float) MathUtils.incValue(value, setting.getIncrement()));
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(getX() + 90, getY() + 18, 60, 10,mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0){
            dragging = false;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
