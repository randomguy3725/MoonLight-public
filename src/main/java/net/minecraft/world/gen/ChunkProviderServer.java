package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider
{
    private static final Logger logger = LogManager.getLogger();
    private final Set<Long> droppedChunksSet = Collections.<Long>newSetFromMap(new ConcurrentHashMap());
    private final Chunk dummyChunk;
    private final IChunkProvider serverChunkGenerator;
    private final IChunkLoader chunkLoader;
    public boolean chunkLoadOverride = true;
    private final LongHashMap<Chunk> id2ChunkMap = new LongHashMap();
    private final List<Chunk> loadedChunks = Lists.newArrayList();
    private final WorldServer worldObj;

    public ChunkProviderServer(WorldServer p_i1520_1_, IChunkLoader p_i1520_2_, IChunkProvider p_i1520_3_)
    {
        this.dummyChunk = new EmptyChunk(p_i1520_1_, 0, 0);
        this.worldObj = p_i1520_1_;
        this.chunkLoader = p_i1520_2_;
        this.serverChunkGenerator = p_i1520_3_;
    }

    public boolean chunkExists(int x, int z)
    {
        return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(x, z));
    }

    public List<Chunk> func_152380_a()
    {
        return this.loadedChunks;
    }

    public void dropChunk(int x, int z)
    {
        if (this.worldObj.provider.canRespawnHere())
        {
            if (!this.worldObj.isSpawnChunk(x, z))
            {
                this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(x, z)));
            }
        }
        else
        {
            this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(x, z)));
        }
    }

    public void unloadAllChunks()
    {
        for (Chunk chunk : this.loadedChunks)
        {
            this.dropChunk(chunk.xPosition, chunk.zPosition);
        }
    }

    public Chunk loadChunk(int chunkX, int chunkZ)
    {
        long i = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
        this.droppedChunksSet.remove(Long.valueOf(i));
        Chunk chunk = this.id2ChunkMap.getValueByKey(i);

        if (chunk == null)
        {
            chunk = this.loadChunkFromFile(chunkX, chunkZ);

            if (chunk == null)
            {
                if (this.serverChunkGenerator == null)
                {
                    chunk = this.dummyChunk;
                }
                else
                {
                    try
                    {
                        chunk = this.serverChunkGenerator.provideChunk(chunkX, chunkZ);
                    }
                    catch (Throwable throwable)
                    {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                        crashreportcategory.addCrashSection("Location", String.format("%d,%d", Integer.valueOf(chunkX), Integer.valueOf(chunkZ)));
                        crashreportcategory.addCrashSection("Position hash", Long.valueOf(i));
                        crashreportcategory.addCrashSection("Generator", this.serverChunkGenerator.makeString());
                        throw new ReportedException(crashreport);
                    }
                }
            }

            this.id2ChunkMap.add(i, chunk);
            this.loadedChunks.add(chunk);
            chunk.onChunkLoad();
            chunk.populateChunk(this, this, chunkX, chunkZ);
        }

        return chunk;
    }

    public Chunk provideChunk(int x, int z)
    {
        Chunk chunk = this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
        return chunk == null ? (!this.worldObj.isFindingSpawnPoint() && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(x, z)) : chunk;
    }

    private Chunk loadChunkFromFile(int x, int z)
    {
        if (this.chunkLoader == null)
        {
            return null;
        }
        else
        {
            try
            {
                Chunk chunk = this.chunkLoader.loadChunk(this.worldObj, x, z);

                if (chunk != null)
                {
                    chunk.setLastSaveTime(this.worldObj.getTotalWorldTime());

                    if (this.serverChunkGenerator != null)
                    {
                        this.serverChunkGenerator.recreateStructures(chunk, x, z);
                    }
                }

                return chunk;
            }
            catch (Exception exception)
            {
                logger.error("Couldn't load chunk", exception);
                return null;
            }
        }
    }

    private void saveChunkExtraData(Chunk chunkIn)
    {
        if (this.chunkLoader != null)
        {
            try
            {
                this.chunkLoader.saveExtraChunkData(this.worldObj, chunkIn);
            }
            catch (Exception exception)
            {
                logger.error("Couldn't save entities", exception);
            }
        }
    }

    private void saveChunkData(Chunk chunkIn)
    {
        if (this.chunkLoader != null)
        {
            try
            {
                chunkIn.setLastSaveTime(this.worldObj.getTotalWorldTime());
                this.chunkLoader.saveChunk(this.worldObj, chunkIn);
            }
            catch (IOException ioexception)
            {
                logger.error("Couldn't save chunk", ioexception);
            }
            catch (MinecraftException minecraftexception)
            {
                logger.error("Couldn't save chunk; already in use by another instance of Minecraft?", minecraftexception);
            }
        }
    }

    public void populate(IChunkProvider chunkProvider, int x, int z)
    {
        Chunk chunk = this.provideChunk(x, z);

        if (!chunk.isTerrainPopulated())
        {
            chunk.func_150809_p();

            if (this.serverChunkGenerator != null)
            {
                this.serverChunkGenerator.populate(chunkProvider, x, z);
                chunk.setChunkModified();
            }
        }
    }

    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z)
    {
        if (this.serverChunkGenerator != null && this.serverChunkGenerator.populateChunk(chunkProvider, chunkIn, x, z))
        {
            Chunk chunk = this.provideChunk(x, z);
            chunk.setChunkModified();
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback)
    {
        int i = 0;
        List<Chunk> list = Lists.newArrayList(this.loadedChunks);

        for (int j = 0; j < list.size(); ++j)
        {
            Chunk chunk = list.get(j);

            if (saveAllChunks)
            {
                this.saveChunkExtraData(chunk);
            }

            if (chunk.needsSaving(saveAllChunks))
            {
                this.saveChunkData(chunk);
                chunk.setModified(false);
                ++i;

                if (i == 24 && !saveAllChunks)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public void saveExtraData()
    {
        if (this.chunkLoader != null)
        {
            this.chunkLoader.saveExtraData();
        }
    }

    public boolean unloadQueuedChunks()
    {
        if (!this.worldObj.disableLevelSaving)
        {
            for (int i = 0; i < 100; ++i)
            {
                if (!this.droppedChunksSet.isEmpty())
                {
                    Long olong = this.droppedChunksSet.iterator().next();
                    Chunk chunk = this.id2ChunkMap.getValueByKey(olong.longValue());

                    if (chunk != null)
                    {
                        chunk.onChunkUnload();
                        this.saveChunkData(chunk);
                        this.saveChunkExtraData(chunk);
                        this.id2ChunkMap.remove(olong.longValue());
                        this.loadedChunks.remove(chunk);
                    }

                    this.droppedChunksSet.remove(olong);
                }
            }

            if (this.chunkLoader != null)
            {
                this.chunkLoader.chunkTick();
            }
        }

        return this.serverChunkGenerator.unloadQueuedChunks();
    }

    public boolean canSave()
    {
        return !this.worldObj.disableLevelSaving;
    }

    public String makeString()
    {
        return "ServerChunkCache: " + this.id2ChunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return this.serverChunkGenerator.getPossibleCreatures(creatureType, pos);
    }

    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        return this.serverChunkGenerator.getStrongholdGen(worldIn, structureName, position);
    }

    public int getLoadedChunkCount()
    {
        return this.id2ChunkMap.getNumHashElements();
    }

    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
    }

    public Chunk provideChunk(BlockPos blockPosIn)
    {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
