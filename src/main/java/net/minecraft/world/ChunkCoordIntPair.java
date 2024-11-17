package net.minecraft.world;

import net.minecraft.util.BlockPos;

public class ChunkCoordIntPair
{
    public final int chunkXPos;
    public final int chunkZPos;
    private int cachedHashCode = 0;

    public ChunkCoordIntPair(int x, int z)
    {
        this.chunkXPos = x;
        this.chunkZPos = z;
    }

    public static long chunkXZ2Int(int x, int z)
    {
        return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
    }

    public int hashCode()
    {
        if (this.cachedHashCode == 0)
        {
            int i = 1664525 * this.chunkXPos + 1013904223;
            int j = 1664525 * (this.chunkZPos ^ -559038737) + 1013904223;
            this.cachedHashCode = i ^ j;
        }

        return this.cachedHashCode;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChunkCoordIntPair chunkcoordintpair))
        {
            return false;
        }
        else
        {
            return this.chunkXPos == chunkcoordintpair.chunkXPos && this.chunkZPos == chunkcoordintpair.chunkZPos;
        }
    }

    public int getCenterXPos()
    {
        return (this.chunkXPos << 4) + 8;
    }

    public int getCenterZPosition()
    {
        return (this.chunkZPos << 4) + 8;
    }

    public int getXStart()
    {
        return this.chunkXPos << 4;
    }

    public int getZStart()
    {
        return this.chunkZPos << 4;
    }

    public int getXEnd()
    {
        return (this.chunkXPos << 4) + 15;
    }

    public int getZEnd()
    {
        return (this.chunkZPos << 4) + 15;
    }

    public BlockPos getBlock(int x, int y, int z)
    {
        return new BlockPos((this.chunkXPos << 4) + x, y, (this.chunkZPos << 4) + z);
    }

    public BlockPos getCenterBlock(int y)
    {
        return new BlockPos(this.getCenterXPos(), y, this.getCenterZPosition());
    }

    public String toString()
    {
        return "[" + this.chunkXPos + ", " + this.chunkZPos + "]";
    }
}
