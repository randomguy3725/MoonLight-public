/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.gui.click.dropdown.component.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.features.values.impl.TextValue;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StringComponent extends Component {
    private final TextValue setting;
    private final Animation input = new DecelerateAnimation(250, 1);
    private boolean inputting;
    private String text = "";
    public StringComponent(TextValue setting) {
        this.setting = setting;
        setHeight(Fonts.interRegular.get(14).getHeight() * 2 + 4);
        input.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        input.setDirection(inputting ? Direction.FORWARDS : Direction.BACKWARDS);
        text = setting.get();
        if (setting.isOnlyNumber() && !NumberUtils.isNumber(text)) {
            text = text.replaceAll("[a-zA-Z]", "");
        }
        String textToDraw = setting.get().isEmpty() && !inputting ? "Empty..." : setting.getText();
        RoundedUtils.drawRound(getX(),getY() + Fonts.interRegular.get(14).getHeight() - 2,getWidth() ,Fonts.interRegular.get(14).getHeight() + 4,2,new Color(ColorUtils.darker(getColorRGB(),0.5f)));
        Fonts.interRegular.get(14).drawString(setting.getName(),getX() + 4,getY(),-1);
        drawTextWithLineBreaks(textToDraw + (inputting && text.length() < 59 && System.currentTimeMillis() % 1000 > 500 ? "|" : ""),getX() + 6,getY() + Fonts.interRegular.get(14).getHeight() + 2,getWidth() - 12);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(getX(),getY() + Fonts.interRegular.get(14).getHeight() + 4,getWidth(),4,mouseX,mouseY) && mouseButton == 0){
            inputting = !inputting;
        } else {
            inputting = false;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (setting.isOnlyNumber() && !NumberUtils.isNumber(String.valueOf(typedChar))) {
            return;
        }
        if (inputting){
            if (keyCode == Keyboard.KEY_BACK) {
                deleteLastCharacter();
            }

            if (text.length() < 18 && (Character.isLetterOrDigit(typedChar) || keyCode == Keyboard.KEY_SPACE)) {
                text += typedChar;
                setting.setText(text);
            }
        }
        super.keyTyped(typedChar, keyCode);
    }
    private void drawTextWithLineBreaks(String text, float x, float y, float maxWidth) {
        String[] lines = text.split("\n");
        float currentY = y;

        for (String line : lines) {
            java.util.List<String> wrappedLines = wrapText(line, 0, maxWidth);
            for (String wrappedLine : wrappedLines) {

                Fonts.interRegular.get(14).drawString(wrappedLine, x, currentY,ColorUtils.interpolateColor2(Color.GRAY,Color.WHITE, (float) input.getOutput()));
                currentY += Fonts.interRegular.get(14).getHeight();
            }
        }
    }

    private java.util.List<String> wrapText(String text, float size, float maxWidth) {
        java.util.List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (Fonts.interRegular.get(14).getStringWidth(word) <= maxWidth) {
                if (Fonts.interRegular.get(14).getStringWidth(currentLine.toString() + word) <= maxWidth) {
                    currentLine.append(word).append(" ");
                } else {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word).append(" ");
                }
            } else {
                if (!currentLine.toString().isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                currentLine = breakAndAddWord(word, currentLine, size, lines);
            }
        }

        if (!currentLine.toString().isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
    private void deleteLastCharacter() {
        if (!text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
            setting.setText(text);
        }
    }
    private StringBuilder breakAndAddWord(String word, StringBuilder currentLine, float maxWidth, List<String> lines) {
        int wordLength = word.length();
        for (int i = 0; i < wordLength; i++) {
            char c = word.charAt(i);
            String nextPart = currentLine.toString() + c;
            if (Fonts.interRegular.get(14).getStringWidth(nextPart) <= maxWidth) {
                currentLine.append(c);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(String.valueOf(c));
            }
        }
        return currentLine;
    }
    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
