package net.optifine.shaders;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class ShadowUtils
{
    public static Iterator<RenderChunk> makeShadowChunkIterator(WorldClient world, double partialTicks, Entity viewEntity, int renderDistanceChunks, ViewFrustum viewFrustum)
    {
        float f = Shaders.getShadowRenderDistance();

        if (f > 0.0F && f < (float)((renderDistanceChunks - 1) * 16))
        {
            int i = MathHelper.ceiling_float_int(f / 16.0F) + 1;
            float f6 = world.getCelestialAngleRadians((float)partialTicks);
            float f1 = Shaders.sunPathRotation * MathHelper.deg2Rad;
            float f2 = f6 > MathHelper.PId2 && f6 < 3.0F * MathHelper.PId2 ? f6 + MathHelper.PI : f6;
            float f3 = -MathHelper.sin(f2);
            float f4 = MathHelper.cos(f2) * MathHelper.cos(f1);
            float f5 = -MathHelper.cos(f2) * MathHelper.sin(f1);
            BlockPos blockpos = new BlockPos(MathHelper.floor_double(viewEntity.posX) >> 4, MathHelper.floor_double(viewEntity.posY) >> 4, MathHelper.floor_double(viewEntity.posZ) >> 4);
            BlockPos blockpos1 = blockpos.add(-f3 * (float)i, -f4 * (float)i, -f5 * (float)i);
            BlockPos blockpos2 = blockpos.add(f3 * (float)renderDistanceChunks, f4 * (float)renderDistanceChunks, f5 * (float)renderDistanceChunks);
            IteratorRenderChunks iteratorrenderchunks = new IteratorRenderChunks(viewFrustum, blockpos1, blockpos2, i, i);
            return iteratorrenderchunks;
        }
        else
        {
            List<RenderChunk> list = Arrays.asList(viewFrustum.renderChunks);
            Iterator<RenderChunk> iterator = list.iterator();
            return iterator;
        }
    }
}
