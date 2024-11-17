package wtf.moonlight.gui.click.dropdown2.component.impl;

import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class BooleanComponent extends Component {
    private final BoolValue setting;
    private final DecelerateAnimation toggleAnimation = new DecelerateAnimation(175, 1);
    private final float distanceToBorder = 5;

    public BooleanComponent(BoolValue setting) {
        this.setting = setting;
        this.toggleAnimation.setDirection(Direction.BACKWARDS);
        setHeight(Fonts.interRegular.get(14).getHeight() + Fonts.interRegular.get(14).getHeight() / 2f);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        this.toggleAnimation.setDirection(this.setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);

        Fonts.interRegular.get(14).drawString(setting.getName(), getX() + distanceToBorder, getY() + getHeight() / 3, -1);

        float width = 20;

        RoundedUtils.drawRound(getX() + getWidth() - width - distanceToBorder, getY() + 2, width, getHeight() - getHeight() / 3, 4, ColorUtils.interpolateColorC(new Color(128, 128, 128, 255), INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get(), (float) toggleAnimation.getOutput()));
        RoundedUtils.drawRound((float) (getX() + getWidth() - (width - width / 2 * toggleAnimation.getOutput()) - distanceToBorder), getY() + 2, width / 2, getHeight() - getHeight() / 3, 4, Color.WHITE);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float width = 20;
        if (MouseUtils.isHovered2(getX() + getWidth() - width - distanceToBorder, getY() + 2, width, getHeight() - getHeight() / 3, mouseX, mouseY) && mouseButton == 0)
            this.setting.set(!this.setting.get());
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean isVisible() {
        return this.setting.canDisplay();
    }
}
