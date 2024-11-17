package wtf.moonlight.gui.click.menu.component.impl;

import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MultiBoxComponent extends Component {
    private final MultiBoolValue setting;
    private final Map<BoolValue, DecelerateAnimation> select = new HashMap<>();

    public MultiBoxComponent(MultiBoolValue setting) {
        this.setting = setting;
        setHeight(26);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 0;
        float heightoff = 0;

        RoundedUtils.drawRound(getX(), getY() + 5, (float) INSTANCE.getMenuGUI().getWidth() / 2 - 30 + 5, getHeight() + heightoff - 5, 4, new Color(35, 35, 35, 255));
        Fonts.interMedium.get(17).drawString(setting.getName(), getX(), getY() + 10, -1);

        for (BoolValue boolValue : setting.getValues()) {
            float off = Fonts.interMedium.get(16).getStringWidth(boolValue.getName()) + 2;
            if (offset + off >= (float) INSTANCE.getMenuGUI().getWidth() / 2 - 30 + 5) {
                offset = 0;
                heightoff += Fonts.interMedium.get(16).getHeight() + 2;
            }
            select.putIfAbsent(boolValue, new DecelerateAnimation(250, 1));
            select.get(boolValue).setDirection(boolValue.get() ? Direction.FORWARDS : Direction.BACKWARDS);

            Fonts.interMedium.get(16).drawString(boolValue.getName(), getX() + offset, getY() + 20 + heightoff, ColorUtils.interpolateColor2(new Color(70, 70, 70, 255), new Color(-1), (float) select.get(boolValue).getOutput()));

            offset += off;
        }

        setHeight(26 + heightoff);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        float offset = 0;
        float heightoff = 0;
        for (BoolValue boolValue : setting.getValues()) {
            float off = Fonts.interMedium.get(16).getStringWidth(boolValue.getName()) + 2;
            if (offset + off >= (float) INSTANCE.getMenuGUI().getWidth() / 2 - 30 + 5) {
                offset = 0;
                heightoff += Fonts.interMedium.get(16).getHeight() + 2;
            }
            if (MouseUtils.isHovered2(getX() + offset, getY() + 20 + heightoff, Fonts.interMedium.get(16).getStringWidth(boolValue.getName()), Fonts.interMedium.get(16).getHeight(), mouseX, mouseY) && mouse == 0) {
                boolValue.set(!boolValue.get());
            }
            offset += off;
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    /*@Override
    public boolean isHovered(float mouseX, float mouseY) {
        return opened && MouseUtils.isHovered2(getX() + 94,
                (getY() + 12 - getHalfTotalHeight()) < INSTANCE.getMenuGUI().getPosY() + 49 ? INSTANCE.getMenuGUI().getPosY() + 49 : (getY() + 12 - getHalfTotalHeight()),
                80f,
                (float) (((((getY() + 12 - (getSize() * 20 * open.getOutput()) / 2f) < INSTANCE.getMenuGUI().getPosY() + 49) ? MathHelper.clamp_float((getY() + 12 - getHalfTotalHeight()) - INSTANCE.getMenuGUI().getPosY() + 49,0,999) : 122)) * open.getOutput()), (int) mouseX, (int) mouseY);
    }*/
    @Override
    public boolean isVisible() {
        return setting.canDisplay();
    }
}
