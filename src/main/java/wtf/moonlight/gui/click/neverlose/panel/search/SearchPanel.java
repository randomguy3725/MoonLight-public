package wtf.moonlight.gui.click.neverlose.panel.search;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.click.IComponent;
import wtf.moonlight.gui.click.neverlose.components.ModuleComponent;
import wtf.moonlight.gui.click.neverlose.panel.Panel;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.InstanceAccess;
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
import java.util.List;

import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

@Getter
public class SearchPanel extends Panel implements IComponent, InstanceAccess {
    @Setter
    private boolean selected;
    private int posX, posY;
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private final Animation animation = new DecelerateAnimation(250,1);
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private final ObjectArrayList<ModuleComponent> moduleComponents = new ObjectArrayList<>();
    private ObjectArrayList<ModuleComponent> filtered = new ObjectArrayList<>();
    private final Animation input = new DecelerateAnimation(250, 1);
    private boolean inputting;
    private String text = "";
    public SearchPanel(ModuleCategory category) {
        super(category);
        for (Module module : INSTANCE.getModuleManager().getModules()) {
            moduleComponents.add(new ModuleComponent(module));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        //animations
        animation.setDirection(isSelected() ? Direction.FORWARDS : Direction.BACKWARDS);
        input.setDirection(inputting ? Direction.FORWARDS : Direction.BACKWARDS);
        //update coordinate
        posX = INSTANCE.getNeverLose().getPosX();
        posY = INSTANCE.getNeverLose().getPosY();
        //render
        if (isSelected()){
            RoundedUtils.drawRoundOutline(posX + 140, posY + 12, 340, (float) 22, 2, 0.1f, ColorUtils.applyOpacity(bgColor4, (float) animation.getOutput()), ColorUtils.applyOpacity(outlineColor, (float) animation.getOutput()));
            //drawTextWithLineBreaks(text + (inputting && text.length() < 67 && System.currentTimeMillis() % 1000 > 500 ? "|" : ""), posX + 144, posY + 21, 180);
            Fonts.interSemiBold.get(18).drawString(text + (inputting && text.length() < 67 && System.currentTimeMillis() % 1000 > 500 ? "|" : ""), posX + 146, posY + 21, Color.WHITE.darker().darker().getRGB());
            if (!inputting && text.isEmpty()) {
                Fonts.neverlose.get(24).drawString("u", posX + 146, posY + 20, textRGB);
                Fonts.interSemiBold.get(18).drawString("Search", posX + 166, posY + 21, textRGB);
            }
            //render module components
            ObjectArrayList<ModuleComponent> filtered = moduleComponents.stream()
                    .filter(moduleComponent -> moduleComponent.getModule().getName().toLowerCase().contains(text.toLowerCase()))
                    .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);
            this.filtered = filtered;

            if (!filtered.isEmpty()) {
                //GL11.glPushMatrix();

                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtils.scissor(getPosX() + 140, getPosY() + 49, 380, 368);

                float left = 0, right = 0;

                for (int i = 0; i < filtered.size(); i++) {
                    ModuleComponent module = filtered.get(i);

                    module.setLeft((i + 1) % 2 != 0);
                    module.setX(module.isLeft() ? posX + 140 : posX + 330);
                    module.setHeight(20);
                    module.setY(posY + 32 + module.getHeight() + ((i + 1) % 2 == 0 ? left : right));
                    float componentOffset = 0;
                    for (Component component2 : module.getComponents()) {
                        if (component2.isVisible())
                            componentOffset += component2.getHeight();
                    }
                    module.setHeight(module.getHeight() + componentOffset);

                    module.drawScreen(mouseX, mouseY);

                    double scroll = getScroll();
                    module.setScroll((int) MathUtils.roundToHalf(scroll));
                    onScroll(30, mouseX, mouseY);

                    maxScroll = Math.max(0, filtered.isEmpty() ? 0 : filtered.get(filtered.size() - 1).getMaxScroll());

                    if ((i + 1) % 2 == 0) {
                        left += 40 + componentOffset;
                    } else {
                        right += 40 + componentOffset;
                    }
                }

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                //GL11.glPopMatrix();
            }
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(posX + 140, posY + 12, 340,22,mouseX,mouseY) && mouseButton == 0){
            inputting = !inputting;
        } else {
            inputting = false;
        }
        filtered.forEach(moduleComponent -> moduleComponent.mouseClicked(mouseX,mouseY,mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        filtered.forEach(moduleComponent -> moduleComponent.mouseReleased(mouseX,mouseY,state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (inputting){
            if (keyCode == Keyboard.KEY_BACK) {
                deleteLastCharacter();
            }
            if (text.length() < 66 && (Character.isLetterOrDigit(typedChar) || keyCode == Keyboard.KEY_SPACE)) {
                text += typedChar;
            }
        }
        filtered.forEach(moduleComponent -> moduleComponent.keyTyped(typedChar,keyCode));
        super.keyTyped(typedChar, keyCode);
    }
    private void deleteLastCharacter() {
        if (!text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
        }
    }
    private StringBuilder breakAndAddWord(String word, StringBuilder currentLine, float maxWidth, List<String> lines) {
        int wordLength = word.length();
        for (int i = 0; i < wordLength; i++) {
            char c = word.charAt(i);
            String nextPart = currentLine.toString() + c;
            if (Fonts.interSemiBold.get(18).getStringWidth(nextPart) <= maxWidth) {
                currentLine.append(c);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(String.valueOf(c));
            }
        }
        return currentLine;
    }
    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        if (MouseUtils.isHovered2(getPosX() + 140, getPosY() + 49, 380, 368, mx, my) && moduleComponents.stream().noneMatch(moduleComponent -> moduleComponent.getComponents().stream().anyMatch(component -> component.isHovered(mx,my)))) {
            rawScroll += (float) Mouse.getDWheel() * 20;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }
    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }
}
