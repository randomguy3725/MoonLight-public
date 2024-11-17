package net.optifine.util;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class RenderChunkUtils
{
    public static int getCountBlocks(RenderChunk renderChunk)
    {
        ExtendedBlockStorage[] aextendedblockstorage = renderChunk.getChunk().getBlockStorageArray();

        if (aextendedblockstorage == null)
        {
            return 0;
        }
        else
        {
            int i = renderChunk.getPosition().getY() >> 4;
            ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[i];
            return extendedblockstorage == null ? 0 : extendedblockstorage.getBlockRefCount();
        }
    }

    public static double getRelativeBufferSize(RenderChunk renderChunk)
    {
        int i = getCountBlocks(renderChunk);
        double d0 = getRelativeBufferSize(i);
        return d0;
    }

    public static double getRelativeBufferSize(int blockCount)
    {
        double d0 = (double)blockCount / 4096.0D;
        d0 = d0 * 0.995D;
        double d1 = d0 * 2.0D - 1.0D;
        d1 = MathHelper.clamp_double(d1, -1.0D, 1.0D);
        return MathHelper.sqrt_double(1.0D - d1 * d1);
    }
}
