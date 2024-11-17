package net.optifine.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.gui.GuiButtonShaderOption;
import net.optifine.util.StrUtils;

public class TooltipProviderShaderOptions extends TooltipProviderOptions
{
    public String[] getTooltipLines(GuiButton btn, int width)
    {
        if (!(btn instanceof GuiButtonShaderOption guibuttonshaderoption))
        {
            return null;
        }
        else
        {
            ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
            String[] astring = this.makeTooltipLines(shaderoption, width);
            return astring;
        }
    }

    private String[] makeTooltipLines(ShaderOption so, int width)
    {
        String s = so.getNameText();
        String s1 = Config.normalize(so.getDescriptionText()).trim();
        String[] astring = this.splitDescription(s1);
        GameSettings gamesettings = Config.getGameSettings();
        String s2 = null;

        if (!s.equals(so.getName()) && gamesettings.advancedItemTooltips)
        {
            s2 = "\u00a78" + Lang.get("of.general.id") + ": " + so.getName();
        }

        String s3 = null;

        if (so.getPaths() != null && gamesettings.advancedItemTooltips)
        {
            s3 = "\u00a78" + Lang.get("of.general.from") + ": " + Config.arrayToString(so.getPaths());
        }

        String s4 = null;

        if (so.getValueDefault() != null && gamesettings.advancedItemTooltips)
        {
            String s5 = so.isEnabled() ? so.getValueText(so.getValueDefault()) : Lang.get("of.general.ambiguous");
            s4 = "\u00a78" + Lang.getDefault() + ": " + s5;
        }

        List<String> list = new ArrayList();
        list.add(s);
        list.addAll(Arrays.asList(astring));

        if (s2 != null)
        {
            list.add(s2);
        }

        if (s3 != null)
        {
            list.add(s3);
        }

        if (s4 != null)
        {
            list.add(s4);
        }

        String[] astring1 = this.makeTooltipLines(width, list);
        return astring1;
    }

    private String[] splitDescription(String desc)
    {
        if (desc.length() <= 0)
        {
            return new String[0];
        }
        else
        {
            desc = StrUtils.removePrefix(desc, "//");
            String[] astring = desc.split("\\. ");

            for (int i = 0; i < astring.length; ++i)
            {
                astring[i] = "- " + astring[i].trim();
                astring[i] = StrUtils.removeSuffix(astring[i], ".");
            }

            return astring;
        }
    }

    private String[] makeTooltipLines(int width, List<String> args)
    {
        FontRenderer fontrenderer = Config.getMinecraft().fontRendererObj;
        List<String> list = new ArrayList();

        for (int i = 0; i < args.size(); ++i)
        {
            String s = args.get(i);

            if (s != null && s.length() > 0)
            {
                for (String s1 : fontrenderer.listFormattedStringToWidth(s, width))
                {
                    list.add(s1);
                }
            }
        }

        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}
