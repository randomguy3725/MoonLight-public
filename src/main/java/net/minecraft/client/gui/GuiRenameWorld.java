package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjglx.input.Keyboard;

public class GuiRenameWorld extends GuiScreen
{
    private final GuiScreen parentScreen;
    private GuiTextField field_146583_f;
    private final String saveName;

    public GuiRenameWorld(GuiScreen parentScreenIn, String saveNameIn)
    {
        this.parentScreen = parentScreenIn;
        this.saveName = saveNameIn;
    }

    public void updateScreen()
    {
        this.field_146583_f.updateCursorCounter();
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectWorld.renameButton")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        ISaveFormat isaveformat = this.mc.getSaveLoader();
        WorldInfo worldinfo = isaveformat.getWorldInfo(this.saveName);
        String s = worldinfo.getWorldName();
        this.field_146583_f = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.field_146583_f.setFocused(true);
        this.field_146583_f.setText(s);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 1)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (button.id == 0)
            {
                ISaveFormat isaveformat = this.mc.getSaveLoader();
                isaveformat.renameWorld(this.saveName, this.field_146583_f.getText().trim());
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.field_146583_f.textboxKeyTyped(typedChar, keyCode);
        this.buttonList.get(0).enabled = this.field_146583_f.getText().trim().length() > 0;

        if (keyCode == 28 || keyCode == 156)
        {
            this.actionPerformed(this.buttonList.get(0));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.field_146583_f.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("selectWorld.renameTitle"), this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
        this.field_146583_f.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
