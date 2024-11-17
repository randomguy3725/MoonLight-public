package net.optifine.shaders.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonDownloadShaders extends GuiButton
{
    public GuiButtonDownloadShaders(int buttonID, int xPos, int yPos)
    {
        super(buttonID, xPos, yPos, 22, 20, "");
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            super.drawButton(mc, mouseX, mouseY);
            ResourceLocation resourcelocation = new ResourceLocation("optifine/textures/icons.png");
            mc.getTextureManager().bindTexture(resourcelocation);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + 3, this.yPosition + 2, 0, 0, 16, 16);
        }
    }
}
