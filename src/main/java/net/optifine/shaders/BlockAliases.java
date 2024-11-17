package net.optifine.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

public class BlockAliases
{
    private static BlockAlias[][] blockAliases = null;
    private static PropertiesOrdered blockLayerPropertes = null;
    private static boolean updateOnResourcesReloaded;

    public static int getBlockAliasId(int blockId, int metadata)
    {
        if (blockAliases == null)
        {
            return blockId;
        }
        else if (blockId >= 0 && blockId < blockAliases.length)
        {
            BlockAlias[] ablockalias = blockAliases[blockId];

            if (ablockalias == null)
            {
                return blockId;
            }
            else
            {
                for (int i = 0; i < ablockalias.length; ++i)
                {
                    BlockAlias blockalias = ablockalias[i];

                    if (blockalias.matches(blockId, metadata))
                    {
                        return blockalias.getBlockAliasId();
                    }
                }

                return blockId;
            }
        }
        else
        {
            return blockId;
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
            if (Reflector.Loader_getActiveModList.exists() && Minecraft.getMinecraft().getResourcePackRepository() == null)
            {
                Config.dbg("[Shaders] Delayed loading of block mappings after resources are loaded");
                updateOnResourcesReloaded = true;
            }
            else
            {
                List<List<BlockAlias>> list = new ArrayList();
                String s = "/shaders/block.properties";
                InputStream inputstream = shaderPack.getResourceAsStream(s);

                if (inputstream != null)
                {
                    loadBlockAliases(inputstream, s, list);
                }

                loadModBlockAliases(list);

                if (list.size() > 0)
                {
                    blockAliases = toArrays(list);
                }
            }
        }
    }

    private static void loadModBlockAliases(List<List<BlockAlias>> listBlockAliases)
    {
        String[] astring = ReflectorForge.getForgeModIds();

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(s, "shaders/block.properties");
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                loadBlockAliases(inputstream, resourcelocation.toString(), listBlockAliases);
            }
            catch (IOException var6)
            {
            }
        }
    }

    private static void loadBlockAliases(InputStream in, String path, List<List<BlockAlias>> listBlockAliases)
    {
        if (in != null)
        {
            try
            {
                in = MacroProcessor.process(in, path);
                Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                Config.dbg("[Shaders] Parsing block mappings: " + path);
                ConnectedParser connectedparser = new ConnectedParser("Shaders");

                for (Object o : properties.keySet())
                {
                    String s = (String) o;
                    String s1 = properties.getProperty(s);

                    if (s.startsWith("layer."))
                    {
                        if (blockLayerPropertes == null)
                        {
                            blockLayerPropertes = new PropertiesOrdered();
                        }

                        blockLayerPropertes.put(s, s1);
                    }
                    else
                    {
                        String s2 = "block.";

                        if (!s.startsWith(s2))
                        {
                            Config.warn("[Shaders] Invalid block ID: " + s);
                        }
                        else
                        {
                            String s3 = StrUtils.removePrefix(s, s2);
                            int i = Config.parseInt(s3, -1);

                            if (i < 0)
                            {
                                Config.warn("[Shaders] Invalid block ID: " + s);
                            }
                            else
                            {
                                MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s1);

                                if (amatchblock != null && amatchblock.length >= 1)
                                {
                                    BlockAlias blockalias = new BlockAlias(i, amatchblock);
                                    addToList(listBlockAliases, blockalias);
                                }
                                else
                                {
                                    Config.warn("[Shaders] Invalid block ID mapping: " + s + "=" + s1);
                                }
                            }
                        }
                    }
                }
            }
            catch (IOException var14)
            {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static void addToList(List<List<BlockAlias>> blocksAliases, BlockAlias ba)
    {
        int[] aint = ba.getMatchBlockIds();

        for (int i = 0; i < aint.length; ++i)
        {
            int j = aint[i];

            while (j >= blocksAliases.size())
            {
                blocksAliases.add(null);
            }

            List<BlockAlias> list = blocksAliases.get(j);

            if (list == null)
            {
                list = new ArrayList();
                blocksAliases.set(j, list);
            }

            BlockAlias blockalias = new BlockAlias(ba.getBlockAliasId(), ba.getMatchBlocks(j));
            list.add(blockalias);
        }
    }

    private static BlockAlias[][] toArrays(List<List<BlockAlias>> listBlocksAliases)
    {
        BlockAlias[][] ablockalias = new BlockAlias[listBlocksAliases.size()][];

        for (int i = 0; i < ablockalias.length; ++i)
        {
            List<BlockAlias> list = listBlocksAliases.get(i);

            if (list != null)
            {
                ablockalias[i] = list.toArray(new BlockAlias[list.size()]);
            }
        }

        return ablockalias;
    }

    public static PropertiesOrdered getBlockLayerPropertes()
    {
        return blockLayerPropertes;
    }

    public static void reset()
    {
        blockAliases = null;
        blockLayerPropertes = null;
    }
}
