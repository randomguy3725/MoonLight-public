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

import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

public class BooleanComponent extends Component {
    private final BoolValue setting;
    private final Animation hover = new DecelerateAnimation(250,1);
    private final Animation enabled = new DecelerateAnimation(250,1);
    public BooleanComponent(BoolValue setting) {
        this.setting = setting;
        setHeight(24);
        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(MouseUtils.isHovered2(getX() + 154,getY() + 16,20,10,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
            RoundedUtils.drawRound(getX() + 4, getY() + 10, 172, .5f, 4, lineColor2);

        Fonts.interSemiBold.get(17).drawString(setting.getName(),getX() + 6,getY() + 20,ColorUtils.interpolateColor2(Color.WHITE.darker().darker(),Color.WHITE, (float) enabled.getOutput()));
        RoundedUtils.drawRound(getX() + 154,getY() + 16,20,10,4,new Color(ColorUtils.interpolateColor2(new Color(ColorUtils.interpolateColor2(boolBgColor,boolBgColor2,(float) enabled.getOutput())),
                new Color(ColorUtils.interpolateColor2(boolBgColor,boolBgColor2,(float) enabled.getOutput())).brighter().brighter(), (float) hover.getOutput())));
        RenderUtils.drawCircle(getX() + 159 + 10 * (float) enabled.getOutput(),getY() + 21,0,360,5,.1f,true, ColorUtils.interpolateColor2(new Color(ColorUtils.interpolateColor2(boolCircleColor2,boolCircleColor,(float) enabled.getOutput())),
                new Color(ColorUtils.interpolateColor2(boolCircleColor.darker().darker(),boolCircleColor,(float) enabled.getOutput())).brighter().brighter(), (float) hover.getOutput()));
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(getX() + 154,getY() + 16,20,10,mouseX,mouseY) && mouseButton == 0){
            setting.toggle();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
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
