package net.optifine;

import java.util.Properties;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

public class CustomLoadingScreen
{
    private final ResourceLocation locationTexture;
    private int scaleMode = 0;
    private int scale = 2;
    private final boolean center;
    private static final int SCALE_DEFAULT = 2;
    private static final int SCALE_MODE_FIXED = 0;
    private static final int SCALE_MODE_FULL = 1;
    private static final int SCALE_MODE_STRETCH = 2;

    public CustomLoadingScreen(ResourceLocation locationTexture, int scaleMode, int scale, boolean center)
    {
        this.locationTexture = locationTexture;
        this.scaleMode = scaleMode;
        this.scale = scale;
        this.center = center;
    }

    public static CustomLoadingScreen parseScreen(String path, int dimId, Properties props)
    {
        ResourceLocation resourcelocation = new ResourceLocation(path);
        int i = parseScaleMode(getProperty("scaleMode", dimId, props));
        int j = i == 0 ? 2 : 1;
        int k = parseScale(getProperty("scale", dimId, props), j);
        boolean flag = Config.parseBoolean(getProperty("center", dimId, props), false);
        CustomLoadingScreen customloadingscreen = new CustomLoadingScreen(resourcelocation, i, k, flag);
        return customloadingscreen;
    }

    private static String getProperty(String key, int dim, Properties props)
    {
        if (props == null)
        {
            return null;
        }
        else
        {
            String s = props.getProperty("dim" + dim + "." + key);

            if (s != null)
            {
                return s;
            }
            else
            {
                s = props.getProperty(key);
                return s;
            }
        }
    }

    private static int parseScaleMode(String str)
    {
        if (str == null)
        {
            return 0;
        }
        else
        {
            str = str.toLowerCase().trim();

            if (str.equals("fixed"))
            {
                return 0;
            }
            else if (str.equals("full"))
            {
                return 1;
            }
            else if (str.equals("stretch"))
            {
                return 2;
            }
            else
            {
                CustomLoadingScreens.warn("Invalid scale mode: " + str);
                return 0;
            }
        }
    }

    private static int parseScale(String str, int def)
    {
        if (str == null)
        {
            return def;
        }
        else
        {
            str = str.trim();
            int i = Config.parseInt(str, -1);

            if (i < 1)
            {
                CustomLoadingScreens.warn("Invalid scale: " + str);
                return def;
            }
            else
            {
                return i;
            }
        }
    }

    public void drawBackground(int width, int height)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        Config.getTextureManager().bindTexture(this.locationTexture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        double d0 = 16 * this.scale;
        double d1 = (double)width / d0;
        double d2 = (double)height / d0;
        double d3 = 0.0D;
        double d4 = 0.0D;

        if (this.center)
        {
            d3 = (d0 - (double)width) / (d0 * 2.0D);
            d4 = (d0 - (double)height) / (d0 * 2.0D);
        }

        switch (this.scaleMode)
        {
            case 1:
                d0 = Math.max(width, height);
                d1 = (double)(this.scale * width) / d0;
                d2 = (double)(this.scale * height) / d0;

                if (this.center)
                {
                    d3 = (double)this.scale * (d0 - (double)width) / (d0 * 2.0D);
                    d4 = (double)this.scale * (d0 - (double)height) / (d0 * 2.0D);
                }

                break;

            case 2:
                d1 = this.scale;
                d2 = this.scale;
                d3 = 0.0D;
                d4 = 0.0D;
        }

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, height, 0.0D).tex(d3, d4 + d2).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(width, height, 0.0D).tex(d3 + d1, d4 + d2).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(width, 0.0D, 0.0D).tex(d3 + d1, d4).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(d3, d4).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
    }
}
