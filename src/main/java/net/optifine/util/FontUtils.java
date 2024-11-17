package net.optifine.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

public class FontUtils
{
    public static Properties readFontProperties(ResourceLocation locationFontTexture)
    {
        String s = locationFontTexture.getResourcePath();
        Properties properties = new PropertiesOrdered();
        String s1 = ".png";

        if (!s.endsWith(s1))
        {
            return properties;
        }
        else
        {
            String s2 = s.substring(0, s.length() - s1.length()) + ".properties";

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(locationFontTexture.getResourceDomain(), s2);
                InputStream inputstream = Config.getResourceStream(Config.getResourceManager(), resourcelocation);

                if (inputstream == null)
                {
                    return properties;
                }

                Config.log("Loading " + s2);
                properties.load(inputstream);
                inputstream.close();
            }
            catch (FileNotFoundException var7)
            {
            }
            catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }

            return properties;
        }
    }

    public static void readCustomCharWidths(Properties props, float[] charWidth)
    {
        for (Object o : props.keySet())
        {
            String s = (String) o;
            String s1 = "width.";

            if (s.startsWith(s1))
            {
                String s2 = s.substring(s1.length());
                int i = Config.parseInt(s2, -1);

                if (i >= 0 && i < charWidth.length)
                {
                    String s3 = props.getProperty(s);
                    float f = Config.parseFloat(s3, -1.0F);

                    if (f >= 0.0F)
                    {
                        charWidth[i] = f;
                    }
                }
            }
        }
    }

    public static float readFloat(Properties props, String key, float defOffset)
    {
        String s = props.getProperty(key);

        if (s == null)
        {
            return defOffset;
        }
        else
        {
            float f = Config.parseFloat(s, Float.MIN_VALUE);

            if (f == Float.MIN_VALUE)
            {
                Config.warn("Invalid value for " + key + ": " + s);
                return defOffset;
            }
            else
            {
                return f;
            }
        }
    }

    public static boolean readBoolean(Properties props, String key, boolean defVal)
    {
        String s = props.getProperty(key);

        if (s == null)
        {
            return defVal;
        }
        else
        {
            String s1 = s.toLowerCase().trim();

            if (!s1.equals("true") && !s1.equals("on"))
            {
                if (!s1.equals("false") && !s1.equals("off"))
                {
                    Config.warn("Invalid value for " + key + ": " + s);
                    return defVal;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return true;
            }
        }
    }

    public static ResourceLocation getHdFontLocation(ResourceLocation fontLoc)
    {
        if (!Config.isCustomFonts())
        {
            return fontLoc;
        }
        else if (fontLoc == null)
        {
            return fontLoc;
        }
        else if (!Config.isMinecraftThread())
        {
            return fontLoc;
        }
        else
        {
            String s = fontLoc.getResourcePath();
            String s1 = "textures/";
            String s2 = "mcpatcher/";

            if (!s.startsWith(s1))
            {
                return fontLoc;
            }
            else
            {
                s = s.substring(s1.length());
                s = s2 + s;
                ResourceLocation resourcelocation = new ResourceLocation(fontLoc.getResourceDomain(), s);
                return Config.hasResource(Config.getResourceManager(), resourcelocation) ? resourcelocation : fontLoc;
            }
        }
    }
}
