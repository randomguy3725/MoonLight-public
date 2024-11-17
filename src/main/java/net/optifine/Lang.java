package net.optifine;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Lang
{
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

    public static void resourcesReloaded()
    {
        Map map = I18n.getLocaleProperties();
        List<String> list = new ArrayList();
        String s = "optifine/lang/";
        String s1 = "en_US";
        String s2 = ".lang";
        list.add(s + s1 + s2);

        if (!Config.getGameSettings().language.equals(s1))
        {
            list.add(s + Config.getGameSettings().language + s2);
        }

        String[] astring = list.toArray(new String[list.size()]);
        loadResources(Config.getDefaultResourcePack(), astring, map);
        IResourcePack[] airesourcepack = Config.getResourcePacks();

        for (int i = 0; i < airesourcepack.length; ++i)
        {
            IResourcePack iresourcepack = airesourcepack[i];
            loadResources(iresourcepack, astring, map);
        }
    }

    private static void loadResources(IResourcePack rp, String[] files, Map localeProperties)
    {
        try
        {
            for (int i = 0; i < files.length; ++i)
            {
                String s = files[i];
                ResourceLocation resourcelocation = new ResourceLocation(s);

                if (rp.resourceExists(resourcelocation))
                {
                    InputStream inputstream = rp.getInputStream(resourcelocation);

                    if (inputstream != null)
                    {
                        loadLocaleData(inputstream, localeProperties);
                    }
                }
            }
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    public static void loadLocaleData(InputStream is, Map localeProperties) throws IOException
    {
        Iterator iterator = IOUtils.readLines(is, Charsets.UTF_8).iterator();
        is.close();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();

            if (!s.isEmpty() && s.charAt(0) != 35)
            {
                String[] astring = Iterables.toArray(splitter.split(s), String.class);

                if (astring != null && astring.length == 2)
                {
                    String s1 = astring[0];
                    String s2 = pattern.matcher(astring[1]).replaceAll("%$1s");
                    localeProperties.put(s1, s2);
                }
            }
        }
    }

    public static String get(String key)
    {
        return I18n.format(key);
    }

    public static String get(String key, String def)
    {
        String s = I18n.format(key);
        return s != null && !s.equals(key) ? s : def;
    }

    public static String getOn()
    {
        return I18n.format("options.on");
    }

    public static String getOff()
    {
        return I18n.format("options.off");
    }

    public static String getFast()
    {
        return I18n.format("options.graphics.fast");
    }

    public static String getFancy()
    {
        return I18n.format("options.graphics.fancy");
    }

    public static String getDefault()
    {
        return I18n.format("generator.default");
    }
}
