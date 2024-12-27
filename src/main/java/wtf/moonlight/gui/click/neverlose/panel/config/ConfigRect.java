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
package wtf.moonlight.gui.click.neverlose.panel.config;

import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.config.Config;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RoundedUtils;


import java.awt.*;

import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

@Getter
public class ConfigRect extends Component {
    private final Config config;
    @Setter
    private float posX, posY, scroll;
    @Setter
    private boolean selected;
    private final Animation hover = new DecelerateAnimation(250,1);
    private final Animation select = new DecelerateAnimation(250,1);
    public ConfigRect(Config config) {
        this.config = config;
        setHeight(36);
        hover.setDirection(Direction.BACKWARDS);
        select.setDirection(Direction.BACKWARDS);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        //coordinate
        float y = getPosY() + scroll;
        //anim
        hover.setDirection(MouseUtils.isHovered2(getPosX() + 290,y + 20,60,18,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        select.setDirection(selected ? Direction.FORWARDS : Direction.BACKWARDS);
        //render
        String name = config.getName().replace(".json","") + (Moonlight.INSTANCE.getConfigManager().getCurrentConfig().equals(config.getName()) ? " (Current Config)" : "");
        RoundedUtils.drawRoundOutline(getPosX(),y + 10,358,getHeight(),4,0.1f,bgColor,new Color(ColorUtils.interpolateColor2(categoryBgColor, categoryBgColor.brighter().brighter(), (float) select.getOutput())));
        Fonts.interSemiBold.get(17).drawString(name,posX + 8,y + 17,-1);
        //button
        RoundedUtils.drawRoundOutline(getPosX() + 290,y + 20,60,18,2,0.1f, new Color(ColorUtils.interpolateColor2(categoryBgColor, categoryBgColor.brighter().brighter(), (float) hover.getOutput())), new Color(iconRGB));
        Fonts.neverlose.get(20).drawString("k",getPosX() + 296,y + 27,-1);
        Fonts.interSemiBold.get(16).drawString("Save",getPosX() + 302 + Fonts.neverlose.get(20).getStringWidth("k"),y + 27,-1);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(getPosX() + 290,getPosY() + scroll + 20,60,18,mouseX,mouseY) && mouseButton == 0) {
            config.saveConfig();
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
    public int getMaxScroll() {
        return (int) (INSTANCE.getNeverLose().getPosY() + 80 + getHeight());
    }
    public boolean isHovered(int mouseX,int mouseY) {
        return MouseUtils.isHovered2(getPosX(),getPosY() + scroll + 10,358,getHeight(),mouseX,mouseY);
    }
}
