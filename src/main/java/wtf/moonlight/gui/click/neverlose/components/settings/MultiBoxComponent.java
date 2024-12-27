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
package wtf.moonlight.gui.click.neverlose.components.settings;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Mouse;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.animations.impl.SmoothStepAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

public class MultiBoxComponent extends Component {
    private final MultiBoolValue setting;
    private final Animation open = new DecelerateAnimation(175, 1);
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private boolean opened;
    private final Map<BoolValue, DecelerateAnimation> select = new HashMap<>();
    public MultiBoxComponent(MultiBoolValue setting) {
        this.setting = setting;
        setHeight(24);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

            RoundedUtils.drawRound(getX() + 4, getY() + 10, 172, .5f, 4, lineColor2);

        Fonts.interSemiBold.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, textRGB);

        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);

        if (open.getOutput() > 0.1) {
            GlStateManager.translate(0, 0, 2f);
            float outlineY = getY() + 11 - getHalfTotalHeight();
            float outlineHeight = (float) ((setting.getValues().size() * 20 + 2) * open.getOutput());
            float y = (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNeverLose().getPosY() + 49 ? INSTANCE.getNeverLose().getPosY() + 49 : (getY() + 12 - getHalfTotalHeight());

            if (setting.getValues().size() > 6){
                GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtils.scissor(getX() + 94,
                        y,
                        80f,
                        getVisibleHeight());
            }

            RoundedUtils.drawRoundOutline(getX() + 94, outlineY, 80f, outlineHeight, 2, .1f,bgColor,outlineColor);

            for (BoolValue boolValue : setting.getValues()) {
                select.putIfAbsent(boolValue,new DecelerateAnimation(250, 1));
                select.get(boolValue).setDirection(boolValue.get() ? Direction.FORWARDS : Direction.BACKWARDS);

                if (boolValue.get()) {
                    float boolValueY = (float) ((getY() + 14 + (setting.getValues().indexOf(boolValue) * 20) * open.getOutput()) - getHalfTotalHeight()) + getScroll();
                    RoundedUtils.drawRound(getX() + 98, boolValueY, 72, 16f, 2,
                            ColorUtils.applyOpacity(bgColor3
                                    , (float) select.get(boolValue).getOutput()));
                }
                Fonts.interSemiBold.get(16).drawString(boolValue.getName(),getX() + 104, (getY() + 21 + (setting.getValues().indexOf(boolValue) * 20 * open.getOutput()) - getHalfTotalHeight()) + getScroll(),ColorUtils.interpolateColor2(Color.WHITE.darker().darker(), new Color(textRGB), (float) select.get(boolValue).getOutput()));

            }
            if (setting.getValues().size() > 6){
                RoundedUtils.drawRound(getX() + 172,
                        (float) (getY() + 12 - getSize() * 20 * open.getOutput() / 2f) + Math.abs((getVisibleHeight() - ((getVisibleHeight() / outlineHeight) * getVisibleHeight())) * (getScroll() / maxScroll)),
                        1f,
                        (getVisibleHeight() / outlineHeight) * getVisibleHeight(),
                        2,
                        categoryBgColor.darker());
            }
            onScroll(30,mouseX,mouseY);
            maxScroll = Math.max(0, setting.getValues().isEmpty() ? 0 : (setting.getValues().size() - 6) * 20);

            if (setting.getValues().size() > 6) {
                GL11.glPopAttrib();
            }
            GlStateManager.translate(0, 0, -2f);
        } else {
            RoundedUtils.drawRoundOutline(getX() + 94, getY() + 12, 80f, 17, 2, .1f,bgColor,outlineColor);
            String enabledText = setting.isEnabled().isEmpty() ? "None" : (setting.isEnabled().length() > 15 ? setting.isEnabled().substring(0, 15) + "..." : setting.isEnabled());
            Fonts.interSemiBold.get(16).drawString(enabledText, getX() + 98, getY() + 15 + Fonts.interSemiBold.get(16).getMiddleOfBox(17), textRGB);
        }

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        if (MouseUtils.isHovered2(getX() + 94,getY() + 14,80f,20,mouseX,mouseY) && mouse == 1){
            opened = !opened;
        }
        if (opened){
            for (BoolValue boolValue : setting.getValues()) {
                if (MouseUtils.isHovered2(getX() + 98, (float) ((getY() + 15 + setting.getValues().indexOf(boolValue) * 20) - ((Math.min(4,(setting.getValues().size() - 1)) * 20) * open.getOutput()) / 2f) + getScroll(), 72, 12, mouseX, mouseY) && mouse == 0) {
                    boolValue.set(!boolValue.get());
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }
    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        if (MouseUtils.isHovered2(getX() + 94,
                (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNeverLose().getPosY() + 49 ? INSTANCE.getNeverLose().getPosY() + 49 : (getY() + 12 - getHalfTotalHeight()),
                80f,
                (float) (((((getY() + 12 - (getSize() * 20 * open.getOutput()) / 2f) < INSTANCE.getNeverLose().getPosY() + 49) ? MathHelper.clamp_float((getY() + 12 - getHalfTotalHeight()) - INSTANCE.getNeverLose().getPosY() + 49,0,999) : 122)) * open.getOutput()), mx, my)) {
            rawScroll += (float) Mouse.getDWheel() * 20;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }
    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }
    @Override
    public boolean isHovered(float mouseX, float mouseY) {
        return opened && MouseUtils.isHovered2(getX() + 94,
                (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNeverLose().getPosY() + 49 ? INSTANCE.getNeverLose().getPosY() + 49 : (getY() + 12 - getHalfTotalHeight()),
                80f,
                (float) (((((getY() + 12 - (getSize() * 20 * open.getOutput()) / 2f) < INSTANCE.getNeverLose().getPosY() + 49) ? MathHelper.clamp_float((getY() + 12 - getHalfTotalHeight()) - INSTANCE.getNeverLose().getPosY() + 49,0,999) : 122)) * open.getOutput()), (int) mouseX, (int) mouseY);
    }
    private float getVisibleHeight() {
        return (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f < INSTANCE.getNeverLose().getPosY() + 49 ? MathHelper.clamp_double(getY() + 12 - getSize() * 20 * open.getOutput() / 2f - INSTANCE.getNeverLose().getPosY() + 49, 0, 999) : 122) * open.getOutput());
    }
    private float getHalfTotalHeight() {
        return (float) ((getSize() * 20 + 2) * open.getOutput() / 2f);
    }
    private int getSize(){
        return Math.min(4, (setting.getValues().size() - 1));
    }
    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
