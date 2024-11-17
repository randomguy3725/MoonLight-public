package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.shaders.Shaders;

public class LayerEnderDragonEyes implements LayerRenderer<EntityDragon>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    private final RenderDragon dragonRenderer;

    public LayerEnderDragonEyes(RenderDragon dragonRendererIn)
    {
        this.dragonRenderer = dragonRendererIn;
    }

    public void doRenderLayer(EntityDragon entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        this.dragonRenderer.bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(1, 1);
        GlStateManager.disableLighting();
        GlStateManager.depthFunc(514);
        int i = 61680;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (Config.isShaders())
        {
            Shaders.beginSpiderEyes();
        }

        Config.getRenderGlobal().renderOverlayEyes = true;
        this.dragonRenderer.getMainModel().render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        Config.getRenderGlobal().renderOverlayEyes = false;

        if (Config.isShaders())
        {
            Shaders.endSpiderEyes();
        }

        this.dragonRenderer.setLightmap(entitylivingbaseIn, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.depthFunc(515);
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
