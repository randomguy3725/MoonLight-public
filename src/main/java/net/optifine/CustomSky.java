package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.render.Blender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;

public class CustomSky
{
    private static CustomSkyLayer[][] worldSkyLayers = null;

    public static void reset()
    {
        worldSkyLayers = null;
    }

    public static void update()
    {
        reset();

        if (Config.isCustomSky())
        {
            worldSkyLayers = readCustomSkies();
        }
    }

    private static CustomSkyLayer[][] readCustomSkies()
    {
        CustomSkyLayer[][] acustomskylayer = new CustomSkyLayer[10][0];
        String s = "mcpatcher/sky/world";
        int i = -1;

        for (int j = 0; j < acustomskylayer.length; ++j)
        {
            String s1 = s + j + "/sky";
            List list = new ArrayList();

            for (int k = 1; k < 1000; ++k)
            {
                String s2 = s1 + k + ".properties";

                try
                {
                    ResourceLocation resourcelocation = new ResourceLocation(s2);
                    InputStream inputstream = Config.getResourceStream(resourcelocation);

                    if (inputstream == null)
                    {
                        break;
                    }

                    Properties properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    Config.dbg("CustomSky properties: " + s2);
                    String s3 = s1 + k + ".png";
                    CustomSkyLayer customskylayer = new CustomSkyLayer(properties, s3);

                    if (customskylayer.isValid(s2))
                    {
                        ResourceLocation resourcelocation1 = new ResourceLocation(customskylayer.source);
                        ITextureObject itextureobject = TextureUtils.getTexture(resourcelocation1);

                        if (itextureobject == null)
                        {
                            Config.log("CustomSky: Texture not found: " + resourcelocation1);
                        }
                        else
                        {
                            customskylayer.textureId = itextureobject.getGlTextureId();
                            list.add(customskylayer);
                            inputstream.close();
                        }
                    }
                }
                catch (FileNotFoundException var15)
                {
                    break;
                }
                catch (IOException ioexception)
                {
                    ioexception.printStackTrace();
                }
            }

            if (list.size() > 0)
            {
                CustomSkyLayer[] acustomskylayer2 = (CustomSkyLayer[]) list.toArray(new CustomSkyLayer[list.size()]);
                acustomskylayer[j] = acustomskylayer2;
                i = j;
            }
        }

        if (i < 0)
        {
            return null;
        }
        else
        {
            int l = i + 1;
            CustomSkyLayer[][] acustomskylayer1 = new CustomSkyLayer[l][0];

            System.arraycopy(acustomskylayer, 0, acustomskylayer1, 0, acustomskylayer1.length);

            return acustomskylayer1;
        }
    }

    public static void renderSky(World world, TextureManager re, float partialTicks)
    {
        if (worldSkyLayers != null)
        {
            int i = world.provider.getDimensionId();

            if (i >= 0 && i < worldSkyLayers.length)
            {
                CustomSkyLayer[] acustomskylayer = worldSkyLayers[i];

                if (acustomskylayer != null)
                {
                    long j = world.getWorldTime();
                    int k = (int)(j % 24000L);
                    float f = world.getCelestialAngle(partialTicks);
                    float f1 = world.getRainStrength(partialTicks);
                    float f2 = world.getThunderStrength(partialTicks);

                    if (f1 > 0.0F)
                    {
                        f2 /= f1;
                    }

                    for (int l = 0; l < acustomskylayer.length; ++l)
                    {
                        CustomSkyLayer customskylayer = acustomskylayer[l];

                        if (customskylayer.isActive(world, k))
                        {
                            customskylayer.render(world, k, f, f1, f2);
                        }
                    }

                    float f3 = 1.0F - f1;
                    Blender.clearBlend(f3);
                }
            }
        }
    }

    public static boolean hasSkyLayers(World world)
    {
        if (worldSkyLayers == null)
        {
            return false;
        }
        else
        {
            int i = world.provider.getDimensionId();

            if (i >= 0 && i < worldSkyLayers.length)
            {
                CustomSkyLayer[] acustomskylayer = worldSkyLayers[i];
                return acustomskylayer != null && acustomskylayer.length > 0;
            }
            else
            {
                return false;
            }
        }
    }
}
