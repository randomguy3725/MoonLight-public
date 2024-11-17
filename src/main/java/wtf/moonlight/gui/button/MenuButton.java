package wtf.moonlight.gui.button;

import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.SmoothStepAnimation;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class MenuButton implements Button {

    public final String text;
    private Animation hoverAnimation;
    public float x, y, width, height;
    public Runnable clickAction;

    public MenuButton(String text) {
        this.text = text;
    }

    @Override
    public void initGui() {
        hoverAnimation = new SmoothStepAnimation(400, 1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        boolean hovered = MouseUtils.isHovered2(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(0, 0, 0, 128);
        RoundedUtils.drawRound(x, y, width, height,6, rectColor);

        Fonts.interRegular.get(15).drawCenteredString(text, x + width / 2f, y + Fonts.interRegular.get(15).getMiddleOfBox(height) + 2, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = MouseUtils.isHovered2(x, y, width, height, mouseX, mouseY);
        if (hovered) clickAction.run();
    }
}