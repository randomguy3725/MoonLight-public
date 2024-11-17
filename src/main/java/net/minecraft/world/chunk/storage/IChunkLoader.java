package net.minecraft.world.chunk.storage;

import java.io.IOException;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IChunkLoader
{
    Chunk loadChunk(World worldIn, int x, int z) throws IOException;

    void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException;

    void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException;

    void chunkTick();

    void saveExtraData();
}
