package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.Moonlight;
import wtf.moonlight.features.modules.impl.visual.Interface;

import java.util.Iterator;

public class LayerCape implements LayerRenderer<AbstractClientPlayer>
{
    private final RenderPlayer playerRenderer;

    public LayerCape(RenderPlayer playerRendererIn)
    {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (!entitylivingbaseIn.isInvisible() && (entitylivingbaseIn.hasPlayerInfo() && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE) && entitylivingbaseIn.getLocationCape() != null || entitylivingbaseIn.getName().equals(Minecraft.getMinecraft().getSession().getUsername()) && Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).isEnabled() && Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).cape.get()))        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if(entitylivingbaseIn.getName().equals(Minecraft.getMinecraft().getSession().getUsername()) && Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).cape.get()){
                this.playerRenderer.bindTexture(new ResourceLocation("moonlight/texture/cape/cape.png"));
            } else {
                this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
            double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
            double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
            float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
            double d3 = MathHelper.sin(f * (float)Math.PI / 180.0F);
            double d4 = -MathHelper.cos(f * (float)Math.PI / 180.0F);
            float f1 = (float)d1 * 10.0F;
            f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;

            if (f2 < 0.0F)
            {
                f2 = 0.0F;
            }

            if (f2 > 165.0F)
            {
                f2 = 165.0F;
            }

            if (f1 < -5.0F)
            {
                f1 = -5.0F;
            }

            float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
            f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

            if (entitylivingbaseIn.isSneaking())
            {
                f1 += 25.0F;
                GlStateManager.translate(0.0F, 0.142F, -0.0178F);
            }

            GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            this.playerRenderer.getMainModel().renderCape(0.0625F);

            if(entitylivingbaseIn.getName().equals(Minecraft.getMinecraft().getSession().getUsername()) && Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).cape.get()){
                this.playerRenderer.bindTexture(new ResourceLocation("moonlight/texture/cape/overlay.png"));
                int rgb = Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).color();
                float alpha = 0.3F;
                float red = (float) (rgb >> 16 & 255) / 255.0F;
                float green = (float) (rgb >> 8 & 255) / 255.0F;
                float blue = (float) (rgb & 255) / 255.0F;
                GL11.glColor4f(red, green, blue, alpha);
                this.playerRenderer.getMainModel().renderCape(0.0625F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (Moonlight.INSTANCE.getModuleManager().getModule(Interface.class).enchanted.get()){
                    for (Iterator<LayerRenderer<AbstractClientPlayer>> var32 = this.playerRenderer.getLayerRenderers().iterator(); var32.hasNext(); GlStateManager.resetColor()) {
                        LayerRenderer var31 = var32.next();
                        if (var31 instanceof LayerArmorBase) {
                            ((LayerArmorBase<?>) var31).renderGlintCape(entitylivingbaseIn, this.playerRenderer.mainModel, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
                        }
                    }
                }
            }
            
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
