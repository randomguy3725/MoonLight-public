package wtf.moonlight.gui.click.menu.component.impl;

import net.minecraft.client.renderer.GlStateManager;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModeComponent extends Component {
    private final ModeValue setting;
    private final Animation open = new DecelerateAnimation(175, 1);
    private boolean opened;
    private final Map<String, DecelerateAnimation> select = new HashMap<>();

    public ModeComponent(ModeValue setting) {
        this.setting = setting;
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        setHeight((float) (((Fonts.interMedium.get(16).getHeight() + 3 * (setting.getModes().length + 1)) * open.getOutput()) + Fonts.interMedium.get(16).getHeight() + 4));
        Fonts.interMedium.get(17).drawString(setting.getName(), getX(), getY() + 5, -1);
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        if (open.getOutput() > 0.1) {

            GlStateManager.translate(0, 0, 2f);

            for (String str : setting.getModes()) {
                select.putIfAbsent(str, new DecelerateAnimation(250, 1));
                select.get(str).setDirection(str.equals(setting.get()) ? Direction.FORWARDS : Direction.BACKWARDS);

                Fonts.interMedium.get(16).drawString(str, getX() + (float) INSTANCE.getMenuGUI().getWidth() / 2 - Fonts.interMedium.get(16).getStringWidth(str) - 35, getY() + 7 + (Arrays.asList(setting.getModes()).indexOf(str) * Fonts.interMedium.get(16).getHeight()) * open.getOutput(), ColorUtils.interpolateColor2(new Color(70, 70, 70, 255), Color.WHITE, (float) select.get(str).getOutput()));
            }

            GlStateManager.translate(0, 0, -2f);
        } else {
            Fonts.interMedium.get(16).drawString(setting.get(), getX() + (float) INSTANCE.getMenuGUI().getWidth() / 2 - Fonts.interMedium.get(16).getStringWidth(setting.get()) - 35, getY() + 7, -1);
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        if (MouseUtils.isHovered2(getX() + (float) INSTANCE.getMenuGUI().getWidth() / 2 - Fonts.interMedium.get(16).getStringWidth(setting.get()) - 35, getY() + 7 + Fonts.interMedium.get(16).getMiddleOfBox(17), Fonts.interMedium.get(16).getStringWidth(setting.get()), Fonts.interMedium.get(16).getHeight(), mouseX, mouseY) && mouse == 1) {
            opened = !opened;
        }
        if (opened) {
            for (String str : setting.getModes()) {
                if (MouseUtils.isHovered2(getX() + (float) INSTANCE.getMenuGUI().getWidth() / 2 - Fonts.interMedium.get(16).getStringWidth(str) - 35, (float) (getY() + 7 + (Arrays.asList(setting.getModes()).indexOf(str) * Fonts.interMedium.get(16).getHeight()) * open.getOutput()), Fonts.interMedium.get(16).getStringWidth(str), Fonts.interMedium.get(16).getHeight(), mouseX, mouseY) && mouse == 0) {
                    setting.set(str);
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.canDisplay();
    }
}

