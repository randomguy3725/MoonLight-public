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
package wtf.moonlight.gui.click.skeet.component.impl.sub.text.box;

import wtf.moonlight.gui.click.skeet.SkeetUI;
import wtf.moonlight.gui.click.skeet.component.ButtonComponent;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.LockedResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;

public abstract class StringComponent extends ButtonComponent implements PredicateComponent {
    private boolean editing;
    private boolean rightTick;
    private String content;
    private final TimerUtils timer = new TimerUtils();

    public StringComponent(Component parent, float x, float y, float width, float height) {
        super(parent, x, y, width, height);
        content  = "";
    }

    @Override
    public void drawComponent(LockedResolution resolution, int mouseX, int mouseY) {
        float x = this.getX();
        float y = this.getY();
        float width = this.getWidth();
        float height = this.getHeight();
        Gui.drawRect(x, y, x + width, y + height, new Color(10, 10, 10, (int) SkeetUI.getAlpha()).getRGB());
        boolean hovered = this.isHovered(mouseX, mouseY);
        if (hovered) {
            Gui.drawRect(x + 0.5f, y + 0.5f, x + width - 0.5f, y + height - 0.5f, new Color(90, 90, 90, (int)SkeetUI.getAlpha()).getRGB());
        }
        if (hovered) {
            RenderUtils.drawGradientRect(x + 1.0f, y + 1.0f, width - 1.0f, height - 1.0f, false, new Color(31, 31, 31, (int)SkeetUI.getAlpha()).getRGB(), new Color(36, 36, 36, (int)SkeetUI.getAlpha()).getRGB());
        } else {
            RenderUtils.drawGradientRect(x + 0.5f, y + 0.5f,  width - 0.5f, height - 0.5f, false, new Color(31, 31, 31, (int)SkeetUI.getAlpha()).getRGB(), new Color(36, 36, 36, (int)SkeetUI.getAlpha()).getRGB());
        }
        if (this.timer.hasTimeElapsed(600L) && this.isEditing()) {
            this.rightTick ^= true;
            this.timer.reset();
        }
        SkeetUI.FONT_RENDERER.drawString(this.getValue(), x + 2.0f, y + 2.5f, new Color(210, 210, 210, (int)SkeetUI.getAlpha()).getRGB());
        SkeetUI.FONT_RENDERER.drawString(this.isEditing() ? (this.rightTick ? "_" : "") : this.getValue(), x + 2.0f + SkeetUI.FONT_RENDERER.getStringWidth(this.getValue()), y + 2.5f, new Color(210, 210, 210, (int)SkeetUI.getAlpha()).getRGB());
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        float posY;
        float posX = this.getX();
        if (this.mouseWithinBounds(mouseX, mouseY, posX + 8.0f, (posY = this.getY()) - 1.0f, posX + this.getParent().getWidth() - 6.0f, posY + 8.0f) && Minecraft.getMinecraft().currentScreen != null) {
            if (mouseButton == 0) {
                this.setEditing(!this.isEditing());
                Keyboard.enableRepeatEvents(this.isEditing());
            }
        } else if (mouseButton == 0 && this.isEditing()) {
            this.setEditing(false);
        }
    }

    @Override
    public void keyTyped(char typed, int keyCode) {
        super.keyTyped(typed, keyCode);

        if (isEditing()) {
            if (keyCode == Keyboard.KEY_BACK && !this.getValue().isEmpty()) {
                content = this.getValue().substring(0, this.getValue().length() - 1);
            }
            if (GuiScreen.isKeyComboCtrlC(keyCode)) {
                GuiScreen.setClipboardString(content);
            }

            if (GuiScreen.isKeyComboCtrlV(keyCode)) {
                content += GuiScreen.getClipboardString();
            }

            if (GuiScreen.isCtrlKeyDown()) return;
            content +=  ChatAllowedCharacters.filterAllowedCharacters(String.valueOf(typed));
            setValue(content);
        }
    }

    public abstract String getValue();

    public abstract void setValue(String value);

    public String getContent() {
        return this.content;
    }

    public boolean isEditing() {
        return this.editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public boolean mouseWithinBounds(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (double)mouseX >= x && (double)mouseX <= x + width && (double)mouseY >= y && (double)mouseY <= y + height;
    }
}

