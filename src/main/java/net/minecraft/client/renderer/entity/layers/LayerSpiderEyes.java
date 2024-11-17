package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.shaders.Shaders;

public class LayerSpiderEyes implements LayerRenderer<EntitySpider>
{
    private static final ResourceLocation SPIDER_EYES = new ResourceLocation("textures/entity/spider_eyes.png");
    private final RenderSpider spiderRenderer;

    public LayerSpiderEyes(RenderSpider spiderRendererIn)
    {
        this.spiderRenderer = spiderRendererIn;
    }

    public void doRenderLayer(EntitySpider entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        this.spiderRenderer.bindTexture(SPIDER_EYES);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(1, 1);

        GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());

        int i = 61680;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (Config.isShaders())
        {
            Shaders.beginSpiderEyes();
        }

        Config.getRenderGlobal().renderOverlayEyes = true;
        this.spiderRenderer.getMainModel().render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        Config.getRenderGlobal().renderOverlayEyes = false;

        if (Config.isShaders())
        {
            Shaders.endSpiderEyes();
        }

        i = entitylivingbaseIn.getBrightnessForRender(partialTicks);
        j = i % 65536;
        k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        this.spiderRenderer.setLightmap(entitylivingbaseIn, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
