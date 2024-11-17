package wtf.moonlight.gui.click.menu.component.impl;

import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class BooleanComponent extends Component {
    private final BoolValue setting;
    private final Animation enabled = new DecelerateAnimation(250, 1);

    public BooleanComponent(BoolValue setting) {
        this.setting = setting;
        setHeight(24);
        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtils.drawRound(getX(), getY() + 10, 12, 12, 5, INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get());
        RenderUtils.drawCheck(getX() + 3, getY() + 16, 2, new Color(255, 255, 255, (int) (255 * enabled.getOutput())).getRGB());
        //RenderUtils.drawCheck(getX() + 3,getY() + 16,2,ColorUtils.interpolateColor2(new Color(0,0,0,0),Color.WHITE, (float) enabled.getOutput()));
        Fonts.interMedium.get(17).drawStringWithShadow(setting.getName(), getX() + 15, getY() + 15, -1);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(getX(), getY() + 10, 12, 12, mouseX, mouseY) && mouseButton == 0) {
            setting.set(!setting.get());
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean isVisible() {
        return setting.canDisplay();
    }
}
