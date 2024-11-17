package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;
import wtf.moonlight.MoonLight;
import wtf.moonlight.events.impl.misc.WorldEvent;

public class GuiDownloadTerrain extends GuiScreen
{
    private final NetHandlerPlayClient netHandlerPlayClient;
    private int progress;
    private final CustomLoadingScreen customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();

    public GuiDownloadTerrain(NetHandlerPlayClient netHandler)
    {
        this.netHandlerPlayClient = netHandler;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    public void initGui()
    {
        this.buttonList.clear();
    }

    public void updateScreen()
    {
        ++this.progress;

        if (this.progress % 20 == 0)
        {
            this.netHandlerPlayClient.addToSendQueue(new C00PacketKeepAlive());
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.customLoadingScreen != null)
        {
            this.customLoadingScreen.drawBackground(this.width, this.height);
        }
        else
        {
            this.drawBackground(0);
        }

        this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
