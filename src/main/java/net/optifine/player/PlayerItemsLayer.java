package net.optifine.player;

import java.util.Map;
import java.util.Set;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.src.Config;

public class PlayerItemsLayer implements LayerRenderer
{
    private RenderPlayer renderPlayer = null;

    public PlayerItemsLayer(RenderPlayer renderPlayer)
    {
        this.renderPlayer = renderPlayer;
    }

    public void doRenderLayer(EntityLivingBase entityLiving, float limbSwing, float limbSwingAmount, float partialTicks, float ticksExisted, float headYaw, float rotationPitch, float scale)
    {
        this.renderEquippedItems(entityLiving, scale, partialTicks);
    }

    protected void renderEquippedItems(EntityLivingBase entityLiving, float scale, float partialTicks)
    {
        if (Config.isShowCapes())
        {
            if (!entityLiving.isInvisible())
            {
                if (entityLiving instanceof AbstractClientPlayer abstractclientplayer)
                {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableRescaleNormal();
                    GlStateManager.enableCull();
                    ModelBiped modelbiped = this.renderPlayer.getMainModel();
                    PlayerConfigurations.renderPlayerItems(modelbiped, abstractclientplayer, scale, partialTicks);
                    GlStateManager.disableCull();
                }
            }
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }

    public static void register(Map renderPlayerMap)
    {
        Set set = renderPlayerMap.keySet();
        boolean flag = false;

        for (Object object : set)
        {
            Object object1 = renderPlayerMap.get(object);

            if (object1 instanceof RenderPlayer renderplayer)
            {
                renderplayer.addLayer(new PlayerItemsLayer(renderplayer));
                flag = true;
            }
        }

        if (!flag)
        {
            Config.warn("PlayerItemsLayer not registered");
        }
    }
}
