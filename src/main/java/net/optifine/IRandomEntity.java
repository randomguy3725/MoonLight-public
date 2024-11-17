package net.optifine;

import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

public interface IRandomEntity
{
    int getId();

    BlockPos getSpawnPosition();

    BiomeGenBase getSpawnBiome();

    String getName();

    int getHealth();

    int getMaxHealth();
}
