package net.optifine.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

public class ItemAliases
{
    private static int[] itemAliases = null;
    private static boolean updateOnResourcesReloaded;
    private static final int NO_ALIAS = Integer.MIN_VALUE;

    public static int getItemAliasId(int itemId)
    {
        if (itemAliases == null)
        {
            return itemId;
        }
        else if (itemId >= 0 && itemId < itemAliases.length)
        {
            int i = itemAliases[itemId];
            return i == Integer.MIN_VALUE ? itemId : i;
        }
        else
        {
            return itemId;
        }
    }

    public static void resourcesReloaded()
    {
        if (updateOnResourcesReloaded)
        {
            updateOnResourcesReloaded = false;
            update(Shaders.getShaderPack());
        }
    }

    public static void update(IShaderPack shaderPack)
    {
        reset();

        if (shaderPack != null)
        {
            if (Reflector.Loader_getActiveModList.exists() && Config.getResourceManager() == null)
            {
                Config.dbg("[Shaders] Delayed loading of item mappings after resources are loaded");
                updateOnResourcesReloaded = true;
            }
            else
            {
                List<Integer> list = new ArrayList();
                String s = "/shaders/item.properties";
                InputStream inputstream = shaderPack.getResourceAsStream(s);

                if (inputstream != null)
                {
                    loadItemAliases(inputstream, s, list);
                }

                loadModItemAliases(list);

                if (list.size() > 0)
                {
                    itemAliases = toArray(list);
                }
            }
        }
    }

    private static void loadModItemAliases(List<Integer> listItemAliases)
    {
        String[] astring = ReflectorForge.getForgeModIds();

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(s, "shaders/item.properties");
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                loadItemAliases(inputstream, resourcelocation.toString(), listItemAliases);
            }
            catch (IOException var6)
            {
            }
        }
    }

    private static void loadItemAliases(InputStream in, String path, List<Integer> listItemAliases)
    {
        if (in != null)
        {
            try
            {
                in = MacroProcessor.process(in, path);
                Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                Config.dbg("[Shaders] Parsing item mappings: " + path);
                ConnectedParser connectedparser = new ConnectedParser("Shaders");

                for (Object o : properties.keySet())
                {
                    String s = (String) o;
                    String s1 = properties.getProperty(s);
                    String s2 = "item.";

                    if (!s.startsWith(s2))
                    {
                        Config.warn("[Shaders] Invalid item ID: " + s);
                    }
                    else
                    {
                        String s3 = StrUtils.removePrefix(s, s2);
                        int i = Config.parseInt(s3, -1);

                        if (i < 0)
                        {
                            Config.warn("[Shaders] Invalid item alias ID: " + i);
                        }
                        else
                        {
                            int[] aint = connectedparser.parseItems(s1);

                            if (aint != null && aint.length >= 1)
                            {
                                for (int j = 0; j < aint.length; ++j)
                                {
                                    int k = aint[j];
                                    addToList(listItemAliases, k, i);
                                }
                            }
                            else
                            {
                                Config.warn("[Shaders] Invalid item ID mapping: " + s + "=" + s1);
                            }
                        }
                    }
                }
            }
            catch (IOException var15)
            {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static void addToList(List<Integer> list, int index, int val)
    {
        while (list.size() <= index)
        {
            list.add(Integer.valueOf(Integer.MIN_VALUE));
        }

        list.set(index, Integer.valueOf(val));
    }

    private static int[] toArray(List<Integer> list)
    {
        int[] aint = new int[list.size()];

        for (int i = 0; i < aint.length; ++i)
        {
            aint[i] = list.get(i).intValue();
        }

        return aint;
    }

    public static void reset()
    {
        itemAliases = null;
    }
}
