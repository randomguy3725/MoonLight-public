package wtf.moonlight.gui.widget.impl;

import org.lwjglx.input.Keyboard;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.gui.widget.Widget;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.RoundedUtils;
import wtf.moonlight.features.modules.Module;

import java.awt.*;

public class KeyBindWidget extends Widget {
    public KeyBindWidget() {
        super("Key Bind");

        this.x = 0;
        this.y = 0.0f;
    }

    @Override
    public void onShader(Shader2DEvent event) {

        if(setting.keyBindMode.is("Type 1")) {
            RoundedUtils.drawRound(renderX, renderY, width, height, 4, new Color(setting.bgColor(),true));
        }
    }

    @Override
    public void render() {

        if(setting.keyBindMode.is("Type 1")) {

            float posX = renderX;
            float posY = renderY;
            float fontSize = 13;
            float padding = 5;
            float iconSizeX = 10;

            String name = "HotKeys";

            RoundedUtils.drawRound(posX, posY, width, height, 4, new Color(setting.bgColor(),true));
            Fonts.interMedium.get(fontSize).drawCenteredString(name, posX - 22 + width / 2, posY + padding + 0.5f + 2, -1);

            float imagePosX = posX + width - iconSizeX - padding;
            Fonts.nursultan.get(fontSize).drawString("C", imagePosX + 2f, posY + 7f + 2, setting.color());

            posY += Fonts.interMedium.get(fontSize).getHeight() + padding * 2;

            float maxWidth = Fonts.interMedium.get(fontSize).getStringWidth(name) + padding * 2;
            float localHeight = Fonts.interMedium.get(fontSize).getHeight() + padding * 2;

            RoundedUtils.drawRound(posX + 0.5f, posY, width - 1, 1.25f, 3, new Color(ColorUtils.darker(setting.color(),0.4f)));
            posY += 3f;

            for (Module module : Moonlight.INSTANCE.getModuleManager().getModules()) {
                if (!(module.getAnimation().getOutput() > 0) || module.getKeyBind() == 0) continue;
                String nameText = module.getName();
                float nameWidth = Fonts.interMedium.get(fontSize).getStringWidth(nameText);

                String bindText = "[" + Keyboard.getKeyName(module.getKeyBind()) + "]";
                float bindWidth = Fonts.interMedium.get(fontSize).getStringWidth(bindText);

                float localWidth = nameWidth + bindWidth + padding * 3;

                Fonts.interMedium.get(fontSize).drawString(nameText, posX + padding, posY + 0.5f + 2, new Color(255, 255, 255, (int) (255 * module.getAnimation().getOutput())).getRGB());
                Fonts.interMedium.get(fontSize).drawString(bindText, posX + width - padding - bindWidth, posY + 0.5f + 2, new Color(255, 255, 255, (int) (255 * module.getAnimation().getOutput())).getRGB());

                if (localWidth > maxWidth) {
                    maxWidth = localWidth;
                }

                posY += (float) ((Fonts.interMedium.get(fontSize).getHeight() + padding) * module.getAnimation().getOutput());
                localHeight += (float) ((Fonts.interMedium.get(fontSize).getHeight() + padding) * module.getAnimation().getOutput());
            }
            width = Math.max(maxWidth, 80);
            height = localHeight + 2.5f;
        }
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Key Bind");
    }
}
