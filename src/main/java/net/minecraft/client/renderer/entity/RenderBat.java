package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderBat extends RenderLiving<EntityBat>
{
    private static final ResourceLocation batTextures = new ResourceLocation("textures/entity/bat.png");

    public RenderBat(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelBat(), 0.25F);
    }

    protected ResourceLocation getEntityTexture(EntityBat entity)
    {
        return batTextures;
    }

    protected void preRenderCallback(EntityBat entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.scale(0.35F, 0.35F, 0.35F);
    }

    protected void rotateCorpse(EntityBat bat, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        if (!bat.getIsBatHanging())
        {
            GlStateManager.translate(0.0F, MathHelper.cos(p_77043_2_ * 0.3F) * 0.1F, 0.0F);
        }
        else
        {
            GlStateManager.translate(0.0F, -0.1F, 0.0F);
        }

        super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
    }
}
