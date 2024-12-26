package wtf.moonlight.gui.click.neverlose.panel.config;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.config.Config;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.gui.click.neverlose.panel.Panel;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.animations.impl.SmoothStepAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.*;

import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

@Getter
public class ConfigPanel extends Panel {
    private Map<ConfigRect, Config> configMap = new HashMap<>();
    private final Animation animation = new DecelerateAnimation(250,1);
    private final Animation hover = new DecelerateAnimation(250,1);
    private final Animation hover2 = new DecelerateAnimation(250,1);
    @Setter
    private boolean selected;
    private int posX, posY;
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private final Animation input = new DecelerateAnimation(250, 1);
    private boolean inputting;
    private String text = "";
    public ConfigPanel(ModuleCategory category) {
        super(category);
        //update configs
        refresh();
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        //set default selection
        if (configMap.keySet().stream().filter(ConfigRect::isSelected).findAny().orElse(null) == null) {
            if (!configMap.keySet().stream().toList().isEmpty()) {
                Objects.requireNonNull(configMap.keySet().stream().filter(configRect -> configRect.getConfig().getName().equals(Moonlight.INSTANCE.getConfigManager().getCurrentConfig())).findFirst().orElse(null)).setSelected(true);
            }
        }
        //update coordinate
        posX = INSTANCE.getNeverLose().getPosX();
        posY = INSTANCE.getNeverLose().getPosY();
        //select anim
        animation.setDirection(selected ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(MouseUtils.isHovered2(posX + 148,posY + 14,70,22,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        hover2.setDirection(MouseUtils.isHovered2(posX + 228,posY + 14,70,22,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        input.setDirection(inputting ? Direction.FORWARDS : Direction.BACKWARDS);
        //render
        if (isSelected()){
            Fonts.interSemiBold.get(16).drawString("- My Items", posX + 148, posY + 64, -1);
            RoundedUtils.drawRound(posX + 148, posY + 80, 360, .5f, 4, lineColor);

            GL11.glPushMatrix();
            RenderUtils.scissor(posX + 148, posY + 80, 360, 338);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            float yOffset = 0;
            for (ConfigRect configRect : configMap.keySet()) {
                configRect.setPosY(posY + 80 + yOffset);
                configRect.setPosX(posX + 150);

                configRect.drawScreen(mouseX, mouseY);

                double scroll = getScroll();
                configRect.setScroll((int) MathUtils.roundToHalf(scroll));
                onScroll(30, mouseX, mouseY);
                maxScroll = Math.max(0, configMap.isEmpty() ? 0 : configMap.keySet().stream().toList().get(configMap.keySet().stream().toList().size() - 1).getMaxScroll());

                yOffset += configRect.getHeight() + 8;
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
            //button
            RoundedUtils.drawRoundOutline(posX + 148, posY + 14, 70, 22, 2, 0.1f, bgColor, new Color(ColorUtils.interpolateColor2(categoryBgColor, categoryBgColor.brighter().brighter(), (float) hover.getOutput())));
            Fonts.neverlose.get(24).drawString("o", posX + 152, posY + 22, -1);
            Fonts.interSemiBold.get(18).drawString("Refresh", posX + 166, posY + 23, -1);

            RoundedUtils.drawRoundOutline(posX + 228, posY + 14, 70, 22, 2, 0.1f, bgColor, new Color(ColorUtils.interpolateColor2(categoryBgColor, categoryBgColor.brighter().brighter(), (float) hover2.getOutput())));
            Fonts.neverlose.get(24).drawString("K", posX + 232, posY + 22, -1);
            Fonts.interSemiBold.get(18).drawString("Create", posX + 246, posY + 23, -1);
            //create
            RoundedUtils.drawRoundOutline(posX + 308, posY + 14, 120, 22, 2, 0.1f, new Color(ColorUtils.interpolateColor2(categoryBgColor, categoryBgColor.brighter().brighter(), (float) input.getOutput())), new Color(ColorUtils.interpolateColor2(categoryBgColor, categoryBgColor.brighter().brighter(), (float) input.getOutput())));
            Fonts.interSemiBold.get(18).drawString(text.isEmpty() && !inputting ? "Input ..." : text + (inputting && text.length() < 65 && System.currentTimeMillis() % 1000 > 500 ? "|" : ""), posX + 312, posY + 22, -1);
        }

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        //handle mouseClick
        if (MouseUtils.isHovered2(posX + 308,posY + 14,120,22,mouseX,mouseY) && mouseButton == 0){
            inputting = !inputting;
        } else {
            inputting = false;
        }
        if (MouseUtils.isHovered2(posX + 148, posY + 14, 70, 22, mouseX, mouseY)) {
            refresh();
        }
        if (MouseUtils.isHovered2(posX + 228,posY + 14,70, 22, mouseX, mouseY)) {
            Moonlight.INSTANCE.getConfigManager().saveConfig(new Config(text + ".json"));
            refresh();
        }
        for (ConfigRect configRect : configMap.keySet()) {
            configRect.mouseClicked(mouseX,mouseY,mouseButton);
            if (configRect.isHovered(mouseX,mouseY) && mouseButton == 0) {
                for (ConfigRect c : configMap.keySet()) {
                    c.setSelected(false);
                }
                configRect.setSelected(true);
            }
            if (mouseButton == 2 && configRect.isHovered(mouseX,mouseY)){
                Moonlight.INSTANCE.getConfigManager().loadConfig(configRect.getConfig());
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    public void refresh(){
        configMap.clear();
        Arrays.stream(Objects.requireNonNull(Moonlight.INSTANCE.getMainDir().listFiles())).filter(file -> file.isFile() && file.getName().endsWith(".json")).forEach(file -> configMap.put(new ConfigRect(new Config(file.getName().replaceFirst(".json",""))),new Config(file.getName().replaceFirst(".json",""))));
        configMap = configMap.values().stream()
                .sorted(Comparator.comparing(Config::getName))
                .collect(LinkedHashMap::new, (map, file) -> map.put(new ConfigRect(new Config(file.getName().replaceFirst(".json",""))),new Config(file.getName().replaceFirst(".json",""))), LinkedHashMap::putAll);
    }
    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (ConfigRect configRect : configMap.keySet()) {
            configRect.mouseReleased(mouseX,mouseY,state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (inputting){
            if (keyCode == Keyboard.KEY_BACK) {
                deleteLastCharacter();
            }

            if (text.length() < 20 && (Character.isLetterOrDigit(typedChar) || keyCode == Keyboard.KEY_SPACE)) {
                text += typedChar;
            }
        }
        super.keyTyped(typedChar, keyCode);
    }
    private void deleteLastCharacter() {
        if (!text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
        }
    }
    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        if (MouseUtils.isHovered2(posX + 148,posY + 80,360, 338, mx, my)) {
            rawScroll += (float) Mouse.getDWheel() * 20;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }
}
