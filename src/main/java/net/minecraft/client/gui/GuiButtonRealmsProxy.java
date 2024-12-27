package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;

public class GuiButtonRealmsProxy extends GuiButton
{
    private final RealmsButton realmsButton;

    public GuiButtonRealmsProxy(RealmsButton realmsButtonIn, int buttonId, int x, int y, String text)
    {
        super(buttonId, x, y, text);
        this.realmsButton = realmsButtonIn;
    }

    public GuiButtonRealmsProxy(RealmsButton realmsButtonIn, int buttonId, int x, int y, String text, int widthIn, int heightIn)
    {
        super(buttonId, x, y, widthIn, heightIn, text);
        this.realmsButton = realmsButtonIn;
    }

    public int getId()
    {
        return super.id;
    }

    public boolean getEnabled()
    {
        return super.enabled;
    }

    public void setEnabled(boolean isEnabled)
    {
        super.enabled = isEnabled;
    }

    public void setText(String text)
    {
        super.displayString = text;
    }

    public float getButtonWidth()
    {
        return super.getButtonWidth();
    }

    public float getPositionY()
    {
        return super.yPosition;
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.mousePressed(mc, mouseX, mouseY))
        {
            this.realmsButton.clicked(mouseX, mouseY);
        }

        return super.mousePressed(mc, mouseX, mouseY);
    }

    public void mouseReleased(int mouseX, int mouseY)
    {
        this.realmsButton.released(mouseX, mouseY);
    }

    public void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
        this.realmsButton.renderBg(mouseX, mouseY);
    }

    public RealmsButton getRealmsButton()
    {
        return this.realmsButton;
    }

    public int getHoverState(boolean mouseOver)
    {
        return this.realmsButton.getYImage(mouseOver);
    }

    public int func_154312_c(boolean p_154312_1_)
    {
        return super.getHoverState(p_154312_1_);
    }

    public float getHeight()
    {
        return this.height;
    }
}
