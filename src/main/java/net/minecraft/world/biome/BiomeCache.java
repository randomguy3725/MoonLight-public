package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache
{
    private final WorldChunkManager chunkManager;
    private long lastCleanupTime;
    private final LongHashMap<BiomeCache.Block> cacheMap = new LongHashMap();
    private final List<BiomeCache.Block> cache = Lists.newArrayList();

    public BiomeCache(WorldChunkManager chunkManagerIn)
    {
        this.chunkManager = chunkManagerIn;
    }

    public BiomeCache.Block getBiomeCacheBlock(int x, int z)
    {
        x = x >> 4;
        z = z >> 4;
        long i = (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
        BiomeCache.Block biomecache$block = this.cacheMap.getValueByKey(i);

        if (biomecache$block == null)
        {
            biomecache$block = new BiomeCache.Block(x, z);
            this.cacheMap.add(i, biomecache$block);
            this.cache.add(biomecache$block);
        }

        biomecache$block.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
        return biomecache$block;
    }

    public BiomeGenBase func_180284_a(int x, int z, BiomeGenBase p_180284_3_)
    {
        BiomeGenBase biomegenbase = this.getBiomeCacheBlock(x, z).getBiomeGenAt(x, z);
        return biomegenbase == null ? p_180284_3_ : biomegenbase;
    }

    public void cleanupCache()
    {
        long i = MinecraftServer.getCurrentTimeMillis();
        long j = i - this.lastCleanupTime;

        if (j > 7500L || j < 0L)
        {
            this.lastCleanupTime = i;

            for (int k = 0; k < this.cache.size(); ++k)
            {
                BiomeCache.Block biomecache$block = this.cache.get(k);
                long l = i - biomecache$block.lastAccessTime;

                if (l > 30000L || l < 0L)
                {
                    this.cache.remove(k--);
                    long i1 = (long)biomecache$block.xPosition & 4294967295L | ((long)biomecache$block.zPosition & 4294967295L) << 32;
                    this.cacheMap.remove(i1);
                }
            }
        }
    }

    public BiomeGenBase[] getCachedBiomes(int x, int z)
    {
        return this.getBiomeCacheBlock(x, z).biomes;
    }

    public class Block
    {
        public float[] rainfallValues = new float[256];
        public BiomeGenBase[] biomes = new BiomeGenBase[256];
        public int xPosition;
        public int zPosition;
        public long lastAccessTime;

        public Block(int x, int z)
        {
            this.xPosition = x;
            this.zPosition = z;
            BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, x << 4, z << 4, 16, 16);
            BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, x << 4, z << 4, 16, 16, false);
        }

        public BiomeGenBase getBiomeGenAt(int x, int z)
        {
            return this.biomes[x & 15 | (z & 15) << 4];
        }
    }
}
