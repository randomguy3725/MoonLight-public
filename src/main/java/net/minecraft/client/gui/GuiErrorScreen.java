package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;

public class GuiErrorScreen extends GuiScreen
{
    private final String field_146313_a;
    private final String field_146312_f;

    public GuiErrorScreen(String p_i46319_1_, String p_i46319_2_)
    {
        this.field_146313_a = p_i46319_1_;
        this.field_146312_f = p_i46319_2_;
    }

    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 140, I18n.format("gui.cancel")));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
        this.drawCenteredString(this.fontRendererObj, this.field_146313_a, this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.field_146312_f, this.width / 2, 110, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        this.mc.displayGuiScreen(null);
    }
}
