package net.minecraft.world.gen.layer;

public class GenLayerIsland extends GenLayer
{
    public GenLayerIsland(long p_i2124_1_)
    {
        super(p_i2124_1_);
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
    {
        int[] aint = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaHeight; ++i)
        {
            for (int j = 0; j < areaWidth; ++j)
            {
                this.initChunkSeed(areaX + j, areaY + i);
                aint[j + i * areaWidth] = this.nextInt(10) == 0 ? 1 : 0;
            }
        }

        if (areaX > -areaWidth && areaX <= 0 && areaY > -areaHeight && areaY <= 0)
        {
            aint[-areaX + -areaY * areaWidth] = 1;
        }

        return aint;
    }
}
