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
package wtf.moonlight.gui.click.neverlose.components;

import org.lwjgl.opengl.GL11;
import wtf.moonlight.features.values.Value;
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
import java.util.HashMap;
import java.util.Map;

import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

public class ElementsManage extends Component {
    public boolean opened;
    private int posX,posY;
    public final Animation open = new DecelerateAnimation(250,1);
    private final Animation hover = new DecelerateAnimation(250,1);
    private final Animation hover2 = new DecelerateAnimation(250,1);
    private final Animation hover3 = new DecelerateAnimation(250,1);
    private final Map<BoolValue, DecelerateAnimation> enableAnimationMap = new HashMap<>();
    public ElementsManage() {
        hover.setDirection(Direction.BACKWARDS);
        hover2.setDirection(Direction.BACKWARDS);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        //update coordinate
        posX = INSTANCE.getNeverLose().espPreviewComponent.getPosX();
        posY = INSTANCE.getNeverLose().espPreviewComponent.getPosY();
        //anim
        hover.setDirection(MouseUtils.isHovered2(posX + INSTANCE.getNeverLose().getWidth() + 70,posY + INSTANCE.getNeverLose().getHeight() - 27.5f,85,10,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        hover2.setDirection(MouseUtils.isHovered2(posX + INSTANCE.getNeverLose().getWidth() + 195.5f, (float) (posY + INSTANCE.getNeverLose().getHeight() - 183.5f * open.getOutput()),10,10,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        //render
        if (open.getOutput() < 0.1){
            Fonts.interSemiBold.get(16).drawCenteredString("Manage Elements", posX + INSTANCE.getNeverLose().getWidth() + 112, posY + INSTANCE.getNeverLose().getHeight() - 24, ColorUtils.interpolateColor2(new Color(textRGB),  new Color(iconRGB), (float) hover.getOutput()));
        }else {
            //enable scissor
            GL11.glPushMatrix();
            RenderUtils.scissor(posX + INSTANCE.getNeverLose().getWidth() + 12, posY + INSTANCE.getNeverLose().getHeight() - 190,200,180);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);

            //rect
            RoundedUtils.drawRoundOutline(posX + INSTANCE.getNeverLose().getWidth() + 12, (float) (posY + INSTANCE.getNeverLose().getHeight() - 190 * open.getOutput()),200,180,4,.1f,bgColor4,outlineColor2);
            //text
            Fonts.interSemiBold.get(16).drawString("Manage Elements", posX + INSTANCE.getNeverLose().getWidth() + 20, (float) (posY + INSTANCE.getNeverLose().getHeight() - 180 * open.getOutput()), textRGB);
            Fonts.neverlose.get(24).drawString("m", posX + INSTANCE.getNeverLose().getWidth() + 194, (float) (posY + INSTANCE.getNeverLose().getHeight() - 182 * open.getOutput()),ColorUtils.interpolateColor2(new Color(textRGB), new Color(iconRGB), (float) hover2.getOutput()));
            //value
            float yOffset = 24, xOffset = 0;
            for (Value value: INSTANCE.getNeverLose().espPreviewComponent.getValues()) {
                if (value instanceof BoolValue boolValue) {
                    float currentWidth = Fonts.interSemiBold.get(16).getStringWidth(boolValue.getName()) + 6;
                    enableAnimationMap.putIfAbsent(boolValue,new DecelerateAnimation(250,1));
                    enableAnimationMap.get(boolValue).setDirection(boolValue.get() ? Direction.FORWARDS : Direction.BACKWARDS);
                    if (xOffset + currentWidth + 4 > 190) {
                        yOffset += 14;
                        xOffset = 0;
                    }
                    RoundedUtils.drawRoundOutline(posX + INSTANCE.getNeverLose().getWidth() + 20 + xOffset, (float) (posY + INSTANCE.getNeverLose().getHeight() - 184 * open.getOutput()) + yOffset,currentWidth,12,2,0.1f,
                            new Color(ColorUtils.interpolateColor2(bgColor4,new Color(iconRGB), (float) enableAnimationMap.get(boolValue).getOutput())),
                            new Color(ColorUtils.interpolateColor2(outlineColor2,new Color(iconRGB), (float) enableAnimationMap.get(boolValue).getOutput())));
                    Fonts.interSemiBold.get(16).drawCenteredString(boolValue.getName(), posX + INSTANCE.getNeverLose().getWidth() + 20 + xOffset + currentWidth / 2f, (float) (posY + INSTANCE.getNeverLose().getHeight() - 180 * open.getOutput()) + yOffset, textRGB);
                    xOffset += currentWidth + 4;
                }
            }

            //disable scissor
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(posX + INSTANCE.getNeverLose().getWidth() + 70,posY + INSTANCE.getNeverLose().getHeight() - 27.5f,85,10,mouseX,mouseY)) {
            opened = true;
        }else if (MouseUtils.isHovered2(posX + INSTANCE.getNeverLose().getWidth() + 195.5f, (float) (posY + INSTANCE.getNeverLose().getHeight() - 183.5f * open.getOutput()),10,10,mouseX,mouseY)) {
            opened = false;
        }
        if (opened) {
            float yOffset = 24, xOffset = 0;
            for (Value value: INSTANCE.getNeverLose().espPreviewComponent.getValues()) {
                if (value instanceof BoolValue boolValue) {
                    float currentWidth = Fonts.interSemiBold.get(16).getStringWidth(boolValue.getName()) + 6;
                    if (xOffset + currentWidth + 4 > 190) {
                        yOffset += 14;
                        xOffset = 0;
                    }
                    if (MouseUtils.isHovered2(posX + INSTANCE.getNeverLose().getWidth() + 20 + xOffset, (float) (posY + INSTANCE.getNeverLose().getHeight() - 184 * open.getOutput()) + yOffset,currentWidth,12,mouseX,mouseY)) {
                        boolValue.set(!boolValue.get());
                    }
                    //RoundedUtils.drawRoundOutline(posX + INSTANCE.getNeverLose().getWidth() + 20 + xOffset, (float) (posY + INSTANCE.getNeverLose().getHeight() - 184 * open.getOutput()) + yOffset,currentWidth,12,2,0.1f,bgColor,new Color(0x00BBFF).darker().darker().darker().darker().darker());
                    //Fonts.interSemiBold.get(16).drawCenteredString(boolValue.getName(), posX + INSTANCE.getNeverLose().getWidth() + 20 + xOffset + currentWidth / 2f, (float) (posY + INSTANCE.getNeverLose().getHeight() - 180 * open.getOutput()) + yOffset, -1);
                    xOffset += currentWidth + 4;
                }
            }
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
}
