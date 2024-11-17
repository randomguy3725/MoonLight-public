package net.optifine.gui;

import com.mojang.authlib.exceptions.InvalidCredentialsException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Random;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;
import net.optifine.Lang;

public class GuiScreenCapeOF extends GuiScreenOF
{
    private final GuiScreen parentScreen;
    private String title;
    private String message;
    private long messageHideTimeMs;
    private String linkUrl;
    private GuiButtonOF buttonCopyLink;
    private final FontRenderer fontRenderer;

    public GuiScreenCapeOF(GuiScreen parentScreenIn)
    {
        this.fontRenderer = Config.getMinecraft().fontRendererObj;
        this.parentScreen = parentScreenIn;
    }

    public void initGui()
    {
        int i = 0;
        this.title = I18n.format("of.options.capeOF.title");
        i = i + 2;
        this.buttonList.add(new GuiButtonOF(210, this.width / 2 - 155, this.height / 6 + 24 * (i >> 1), 150, 20, I18n.format("of.options.capeOF.openEditor")));
        this.buttonList.add(new GuiButtonOF(220, this.width / 2 - 155 + 160, this.height / 6 + 24 * (i >> 1), 150, 20, I18n.format("of.options.capeOF.reloadCape")));
        i = i + 6;
        this.buttonCopyLink = new GuiButtonOF(230, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, I18n.format("of.options.capeOF.copyEditorLink"));
        this.buttonCopyLink.visible = this.linkUrl != null;
        this.buttonList.add(this.buttonCopyLink);
        i = i + 4;
        this.buttonList.add(new GuiButtonOF(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("gui.done")));
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 200)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }

            if (button.id == 210)
            {
                try
                {
                    String s = this.mc.getSession().getProfile().getName();
                    String s1 = this.mc.getSession().getProfile().getId().toString().replace("-", "");
                    String s2 = this.mc.getSession().getToken();
                    Random random = new Random();
                    Random random1 = new Random(System.identityHashCode(new Object()));
                    BigInteger biginteger = new BigInteger(128, random);
                    BigInteger biginteger1 = new BigInteger(128, random1);
                    BigInteger biginteger2 = biginteger.xor(biginteger1);
                    String s3 = biginteger2.toString(16);
                    this.mc.getSessionService().joinServer(this.mc.getSession().getProfile(), s2, s3);
                    String s4 = "https://optifine.net/capeChange?u=" + s1 + "&n=" + s + "&s=" + s3;
                    boolean flag = Config.openWebLink(new URI(s4));

                    if (flag)
                    {
                        this.showMessage(Lang.get("of.message.capeOF.openEditor"), 10000L);
                    }
                    else
                    {
                        this.showMessage(Lang.get("of.message.capeOF.openEditorError"), 10000L);
                        this.setLinkUrl(s4);
                    }
                }
                catch (InvalidCredentialsException invalidcredentialsexception)
                {
                    Config.showGuiMessage(I18n.format("of.message.capeOF.error1"), I18n.format("of.message.capeOF.error2", invalidcredentialsexception.getMessage()));
                    Config.warn("Mojang authentication failed");
                    Config.warn(invalidcredentialsexception.getClass().getName() + ": " + invalidcredentialsexception.getMessage());
                }
                catch (Exception exception)
                {
                    Config.warn("Error opening OptiFine cape link");
                    Config.warn(exception.getClass().getName() + ": " + exception.getMessage());
                }
            }

            if (button.id == 220)
            {
                this.showMessage(Lang.get("of.message.capeOF.reloadCape"), 15000L);

                if (this.mc.thePlayer != null)
                {
                    long i = 15000L;
                    long j = System.currentTimeMillis() + i;
                    this.mc.thePlayer.setReloadCapeTimeMs(j);
                }
            }

            if (button.id == 230 && this.linkUrl != null)
            {
                setClipboardString(this.linkUrl);
            }
        }
    }

    private void showMessage(String msg, long timeMs)
    {
        this.message = msg;
        this.messageHideTimeMs = System.currentTimeMillis() + timeMs;
        this.setLinkUrl(null);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);

        if (this.message != null)
        {
            this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, this.height / 6 + 60, 16777215);

            if (System.currentTimeMillis() > this.messageHideTimeMs)
            {
                this.message = null;
                this.setLinkUrl(null);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
        this.buttonCopyLink.visible = linkUrl != null;
    }
}
