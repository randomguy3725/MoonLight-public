package wtf.moonlight.gui.click.neverlose.components.settings;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Mouse;
import wtf.moonlight.features.values.impl.ModeValue;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glEnable;
import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

public class ModeComponent extends Component {
    private final ModeValue setting;
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private final Animation open = new DecelerateAnimation(175, 1);
    private boolean opened;
    private final Map<String, DecelerateAnimation> select = new HashMap<>();
    public ModeComponent(ModeValue setting) {
        this.setting = setting;
        setHeight(24);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {

            RoundedUtils.drawRound(getX() + 4, getY() + 10, 172, .5f, 4, lineColor2);

        Fonts.interSemiBold.get(17).drawString(setting.getName(),getX() + 6,getY() + 20,textRGB);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        if (open.getOutput() > 0.1){
            float totalHeight = (float) ((setting.getModes().length * 20 + 2) * open.getOutput());
            float y = (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getNeverLose().getPosY() + 49 ? INSTANCE.getNeverLose().getPosY() + 49 : (getY() + 12 - getHalfTotalHeight());

            GlStateManager.translate(0,0,2f);
            if (setting.getModes().length > 6){
                GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
                glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtils.scissor(getX() + 94, y, 80f, getVisibleHeight());
            }

            RoundedUtils.drawRoundOutline(getX() + 94, getY() + 12 - getHalfTotalHeight(), 80f, totalHeight,2,.1f,bgColor,outlineColor);
            for (String str : setting.getModes()){
                select.putIfAbsent(str,new DecelerateAnimation(250, 1));
                select.get(str).setDirection(str.equals(setting.get()) ? Direction.FORWARDS : Direction.BACKWARDS);

                if (str.equals(setting.get())){
                    RoundedUtils.drawRound(getX() + 98, ((float) (getY() + 15 + (Arrays.asList(setting.getModes()).indexOf(str) * 20) * open.getOutput()) - getHalfTotalHeight()) + getScroll(), 72F,16f,2,
                            ColorUtils.applyOpacity(bgColor3
                            , (float) select.get(setting.get()).getOutput()));
                }
                Fonts.interSemiBold.get(16).drawString(str,getX() + 104,getY() + 21 + (Arrays.asList(setting.getModes()).indexOf(str) * 20) * open.getOutput() - getHalfTotalHeight() + getScroll(),ColorUtils.interpolateColor2(Color.WHITE.darker().darker(), new Color(textRGB), (float) select.get(str).getOutput()));
            }

            if (setting.getModes().length > 6){
                RoundedUtils.drawRound(getX() + 172,
                        (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f) + Math.abs((getVisibleHeight() - ((getVisibleHeight() / totalHeight) * getVisibleHeight())) * (getScroll() / maxScroll))),
                        1f,
                        (getVisibleHeight() / totalHeight) * getVisibleHeight(),
                        2,
                        categoryBgColor);
            }
            onScroll(30,mouseX,mouseY);
            maxScroll = Math.max(0, setting.getModes().length == 0 ? 0 : (setting.getModes().length - 6) * 20);

            if (setting.getModes().length > 6) {
                GL11.glPopAttrib();
            }

            GlStateManager.translate(0,0,-2f);
        } else {
            RoundedUtils.drawRoundOutline(getX() + 94,getY() + 13,80f,17,2,.1f,bgColor,outlineColor);
            Fonts.interSemiBold.get(16).drawString(setting.get(),getX() + 98,getY() + 15 + Fonts.interSemiBold.get(16).getMiddleOfBox(17),textRGB);
        }
        super.drawScreen(mouseX, mouseY);
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        if (MouseUtils.isHovered2(getX() + 94,getY() + 14,80f,20,mouseX,mouseY) && mouse == 1){
            opened = !opened;
        }
        if (opened){
            for (String str : setting.getModes()) {
                if (MouseUtils.isHovered2(getX() + 98, ((getY() + 15 + Arrays.asList(setting.getModes()).indexOf(str) * 20) - getHalfTotalHeight()) + getScroll(), 52, 12, mouseX, mouseY) && mouse == 0) {
                    setting.set(str);
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }
    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        float halfTotalHeight = (float) ((getSize() * 20 * open.getOutput()) / 2f);
        float y = (getY() + 12 - halfTotalHeight / 2f) < INSTANCE.getNeverLose().getPosY() + 49 ? INSTANCE.getNeverLose().getPosY() + 49 : (getY() + 12 - halfTotalHeight);
        float visibleHeight = getVisibleHeight();

        if (MouseUtils.isHovered2(getX() + 94,
                y,
                80f,
                visibleHeight, mx, my)) {
            rawScroll += (float) Mouse.getDWheel() * 20;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }

    private float getVisibleHeight() {
        return (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f < INSTANCE.getNeverLose().getPosY() + 49 ? MathHelper.clamp_double(getY() + 12 - getSize() * 20 * open.getOutput() / 2f - INSTANCE.getNeverLose().getPosY() + 49, 0, 999) : 122) * open.getOutput());
    }
    private float getHalfTotalHeight() {
        return (float) ((getSize() * 20 * open.getOutput()) / 2f);
    }
    private int getSize(){
        return Math.min(4, (setting.getModes().length - 1));
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
                (float) ((getY() + 12 - getSize() * 20 * open.getOutput() / 2f < INSTANCE.getNeverLose().getPosY() + 49 ? MathHelper.clamp_double(getY() + 12 - getHalfTotalHeight() - INSTANCE.getNeverLose().getPosY() + 49,0,999) : 122) * open.getOutput()), (int) mouseX, (int) mouseY);
    }
    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
