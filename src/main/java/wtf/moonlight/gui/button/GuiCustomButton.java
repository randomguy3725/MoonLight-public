/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.jetbrains.annotations.NotNull;
import wtf.moonlight.gui.font.FontRenderer;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.SmoothStepAnimation;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class GuiCustomButton extends GuiButton {

    private Animation hoverAnimation = new SmoothStepAnimation(400, 1);
    public FontRenderer fontRenderer = Fonts.interRegular.get(15);
    public float radius = 8;
    public Runnable clickAction;

    public GuiCustomButton(String text, int buttonId, float xPosition, float yPosition, float radius, FontRenderer fontRenderer) {
        super(buttonId,xPosition,yPosition,200,20,text);
        this.radius = radius;
        this.fontRenderer = fontRenderer;
    }

    public GuiCustomButton(String text, int buttonId, float xPosition, float yPosition, float width, float height, float radius, FontRenderer fontRenderer) {
        super(buttonId,xPosition,yPosition,width,height,text);
        this.radius = radius;
        this.fontRenderer = fontRenderer;
    }

    public GuiCustomButton(String text, int buttonId, float xPosition, float yPosition) {
        super(buttonId,xPosition,yPosition,200,20,text);
    }

    public GuiCustomButton(String buttonText,
                           int id,
                           int x,
                           int y,
                           int width,
                           int height,
                           int radius,
                           @NotNull FontRenderer fontRenderer) {
        super(id, x, y, width, height, buttonText);
        this.radius = radius;
        this.fontRenderer = fontRenderer;
    }

    public void drawButton(int mouseX, int mouseY){
        drawButton(Minecraft.getMinecraft(),mouseX,mouseY);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {

        boolean hovered = MouseUtils.isHovered2(xPosition, yPosition, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(0, 0, 0, 128);
        RoundedUtils.drawRound(xPosition, yPosition, width, height,radius, rectColor);

        fontRenderer.drawCenteredString(displayString, xPosition + width / 2f, yPosition + fontRenderer.getMiddleOfBox(height) + 2, -1);
    }
}