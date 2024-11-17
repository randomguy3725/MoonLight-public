package net.optifine.gui;

import java.awt.Rectangle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.gui.GuiButtonDownloadShaders;
import net.optifine.shaders.gui.GuiButtonEnumShaderOption;

public class TooltipProviderEnumShaderOptions implements TooltipProvider
{
    public Rectangle getTooltipBounds(GuiScreen guiScreen, int x, int y)
    {
        int i = guiScreen.width - 450;
        int j = 35;

        if (i < 10)
        {
            i = 10;
        }

        if (y <= j + 94)
        {
            j += 100;
        }

        int k = i + 150 + 150;
        int l = j + 84 + 10;
        return new Rectangle(i, j, k - i, l - j);
    }

    public boolean isRenderBorder()
    {
        return true;
    }

    public String[] getTooltipLines(GuiButton btn, int width)
    {
        if (btn instanceof GuiButtonDownloadShaders)
        {
            return TooltipProviderOptions.getTooltipLines("of.options.shaders.DOWNLOAD");
        }
        else if (!(btn instanceof GuiButtonEnumShaderOption guibuttonenumshaderoption))
        {
            return null;
        }
        else
        {
            EnumShaderOption enumshaderoption = guibuttonenumshaderoption.getEnumShaderOption();
            String[] astring = this.getTooltipLines(enumshaderoption);
            return astring;
        }
    }

    private String[] getTooltipLines(EnumShaderOption option)
    {
        return TooltipProviderOptions.getTooltipLines(option.getResourceKey());
    }
}
