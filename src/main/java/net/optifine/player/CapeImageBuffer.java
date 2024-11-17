package net.optifine.player;

import java.awt.image.BufferedImage;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer extends ImageBufferDownload
{
    private AbstractClientPlayer player;
    private final ResourceLocation resourceLocation;
    private boolean elytraOfCape;

    public CapeImageBuffer(AbstractClientPlayer player, ResourceLocation resourceLocation)
    {
        this.player = player;
        this.resourceLocation = resourceLocation;
    }

    public BufferedImage parseUserSkin(BufferedImage imageRaw)
    {
        BufferedImage bufferedimage = CapeUtils.parseCape(imageRaw);
        this.elytraOfCape = CapeUtils.isElytraCape(imageRaw, bufferedimage);
        return bufferedimage;
    }

    public void skinAvailable()
    {
        if (this.player != null)
        {
            this.player.setLocationOfCape(this.resourceLocation);
            this.player.setElytraOfCape(this.elytraOfCape);
        }

        this.cleanup();
    }

    public void cleanup()
    {
        this.player = null;
    }

    public boolean isElytraOfCape()
    {
        return this.elytraOfCape;
    }
}
