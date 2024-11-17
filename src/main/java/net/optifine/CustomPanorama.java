package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.MathUtils;
import net.optifine.util.PropertiesOrdered;

public class CustomPanorama
{
    private static CustomPanoramaProperties customPanoramaProperties = null;
    private static final Random random = new Random();

    public static CustomPanoramaProperties getCustomPanoramaProperties()
    {
        return customPanoramaProperties;
    }

    public static void update()
    {
        customPanoramaProperties = null;
        String[] astring = getPanoramaFolders();

        if (astring.length > 1)
        {
            Properties[] aproperties = getPanoramaProperties(astring);
            int[] aint = getWeights(aproperties);
            int i = getRandomIndex(aint);
            String s = astring[i];
            Properties properties = aproperties[i];

            if (properties == null)
            {
                properties = aproperties[0];
            }

            if (properties == null)
            {
                properties = new PropertiesOrdered();
            }

            CustomPanoramaProperties custompanoramaproperties = new CustomPanoramaProperties(s, properties);
            customPanoramaProperties = custompanoramaproperties;
        }
    }

    private static String[] getPanoramaFolders()
    {
        List<String> list = new ArrayList();
        list.add("textures/gui/title/background");

        for (int i = 0; i < 100; ++i)
        {
            String s = "optifine/gui/background" + i;
            String s1 = s + "/panorama_0.png";
            ResourceLocation resourcelocation = new ResourceLocation(s1);

            if (Config.hasResource(resourcelocation))
            {
                list.add(s);
            }
        }

        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }

    private static Properties[] getPanoramaProperties(String[] folders)
    {
        Properties[] aproperties = new Properties[folders.length];

        for (int i = 0; i < folders.length; ++i)
        {
            String s = folders[i];

            if (i == 0)
            {
                s = "optifine/gui";
            }
            else
            {
                Config.dbg("CustomPanorama: " + s);
            }

            ResourceLocation resourcelocation = new ResourceLocation(s + "/background.properties");

            try
            {
                InputStream inputstream = Config.getResourceStream(resourcelocation);

                if (inputstream != null)
                {
                    Properties properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    Config.dbg("CustomPanorama: " + resourcelocation.getResourcePath());
                    aproperties[i] = properties;
                    inputstream.close();
                }
            }
            catch (IOException var7)
            {
            }
        }

        return aproperties;
    }

    private static int[] getWeights(Properties[] propertiess)
    {
        int[] aint = new int[propertiess.length];

        for (int i = 0; i < aint.length; ++i)
        {
            Properties properties = propertiess[i];

            if (properties == null)
            {
                properties = propertiess[0];
            }

            if (properties == null)
            {
                aint[i] = 1;
            }
            else
            {
                String s = properties.getProperty("weight", null);
                aint[i] = Config.parseInt(s, 1);
            }
        }

        return aint;
    }

    private static int getRandomIndex(int[] weights)
    {
        int i = MathUtils.getSum(weights);
        int j = random.nextInt(i);
        int k = 0;

        for (int l = 0; l < weights.length; ++l)
        {
            k += weights[l];

            if (k > j)
            {
                return l;
            }
        }

        return weights.length - 1;
    }
}
