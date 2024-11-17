package wtf.moonlight.gui.click.dropdown.component.impl;

import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class ModeComponent extends Component {
    private final ModeValue setting;

    public ModeComponent(ModeValue setting) {
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 0;
        float heightoff = 0;

        RoundedUtils.drawRound(getX() + offset, getY() + Fonts.interRegular.get(15).getHeight() + 2, getWidth() - 5, heightoff, 4, new Color(50, 50, 108, 200));
        Fonts.interRegular.get(15).drawString(setting.getName(), getX() + 4, getY(), -1);

        for (String text : setting.getModes()) {
            float off = Fonts.interRegular.get(13).getStringWidth(text) + 2;
            if (offset + off >= (getWidth() - 5)) {
                offset = 0;
                heightoff += 8;
            }

            if (text.equals(setting.get())) {
                Fonts.interRegular.get(13).drawString(text, getX() + offset + 8, getY() + Fonts.interRegular.get(15).getHeight() + 2 + heightoff,
                        INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get().getRGB());
            } else {
                Fonts.interRegular.get(13).drawString(text, getX() + offset + 8, getY() + Fonts.interRegular.get(15).getHeight() + 2 + heightoff,
                        Color.GRAY.getRGB());
            }

            offset += off;

        }

        setHeight(Fonts.interRegular.get(15).getHeight() + 10 + heightoff);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        float offset = 0;
        float heightoff = 0;
        for (String text : setting.getModes()) {
            float off = Fonts.interRegular.get(13).getStringWidth(text) + 2;
            if (offset + off >= (getWidth() - 5)) {
                offset = 0;
                heightoff += 8;
            }
            if (MouseUtils.isHovered2(getX() + offset + 8, getY() + Fonts.interRegular.get(15).getHeight() + 2 + heightoff, Fonts.interRegular.get(13).getStringWidth(text), Fonts.interRegular.get(13).getHeight(), mouseX, mouseY) && mouse == 0) {
                setting.set(text);
            }
            offset += off;
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.canDisplay();
    }
}
