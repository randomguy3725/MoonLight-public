package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.optifine.gui.GuiButtonOF;
import net.optifine.gui.GuiScreenCapeOF;

public class GuiCustomizeSkin extends GuiScreen
{
    private final GuiScreen parentScreen;
    private String title;

    public GuiCustomizeSkin(GuiScreen parentScreenIn)
    {
        this.parentScreen = parentScreenIn;
    }

    public void initGui()
    {
        int i = 0;
        this.title = I18n.format("options.skinCustomisation.title");

        for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values())
        {
            this.buttonList.add(new GuiCustomizeSkin.ButtonPart(enumplayermodelparts.getPartId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, enumplayermodelparts));
            ++i;
        }

        if (i % 2 == 1)
        {
            ++i;
        }

        this.buttonList.add(new GuiButtonOF(210, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("of.options.skinCustomisation.ofCape")));
        i = i + 2;
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("gui.done")));
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 210)
            {
                this.mc.displayGuiScreen(new GuiScreenCapeOF(this));
            }

            if (button.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (button instanceof GuiCustomizeSkin.ButtonPart)
            {
                EnumPlayerModelParts enumplayermodelparts = ((GuiCustomizeSkin.ButtonPart)button).playerModelParts;
                this.mc.gameSettings.switchModelPartEnabled(enumplayermodelparts);
                button.displayString = this.func_175358_a(enumplayermodelparts);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String func_175358_a(EnumPlayerModelParts playerModelParts)
    {
        String s;

        if (this.mc.gameSettings.getModelParts().contains(playerModelParts))
        {
            s = I18n.format("options.on");
        }
        else
        {
            s = I18n.format("options.off");
        }

        return playerModelParts.func_179326_d().getFormattedText() + ": " + s;
    }

    class ButtonPart extends GuiButton
    {
        private final EnumPlayerModelParts playerModelParts;

        private ButtonPart(int p_i45514_2_, int p_i45514_3_, int p_i45514_4_, int p_i45514_5_, int p_i45514_6_, EnumPlayerModelParts playerModelParts)
        {
            super(p_i45514_2_, p_i45514_3_, p_i45514_4_, p_i45514_5_, p_i45514_6_, GuiCustomizeSkin.this.func_175358_a(playerModelParts));
            this.playerModelParts = playerModelParts;
        }
    }
}
