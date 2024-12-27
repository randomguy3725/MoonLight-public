/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.gui.altmanager.utils;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;
import java.util.List;


@SuppressWarnings({"rawtypes", "unchecked"})
public final class FakeWorld extends World {

    public FakeWorld() {
        super(new FakeSaveHandler(), new WorldInfo(new NBTTagCompound()), new FakeWorldProvider(), null, false);
    }

    @Override
    public void markBlocksDirtyVertical(final int par1, final int par2, final int par3, final int par4) {
    }

    @Override
    public void markBlockRangeForRenderUpdate(final int p_147458_1_,
                                              final int p_147458_2_,
                                              final int p_147458_3_,
                                              final int p_147458_4_,
                                              final int p_147458_5_,
                                              final int p_147458_6_) {
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return true;
    }

    @Override
    public float getLightBrightness(BlockPos pos) {
        return 14;
    }

    @Override
    public boolean isDaytime() {
        return true;
    }

    @Override
    public MovingObjectPosition rayTraceBlocks(final Vec3 par1Vec3, final Vec3 par2Vec3) {
        return null;
    }

    @Override
    public MovingObjectPosition rayTraceBlocks(final Vec3 par1Vec3, final Vec3 par2Vec3, final boolean par3) {
        return null;
    }

    @Override
    public MovingObjectPosition rayTraceBlocks(final Vec3 p_147447_1_, final Vec3 p_147447_2_, final boolean p_147447_3_, final boolean p_147447_4_, final boolean p_147447_5_) {
        return null;
    }

    @Override
    public void playSoundAtEntity(final Entity par1Entity, final String par2Str, final float par3, final float par4) {
    }

    @Override
    public void playSoundToNearExcept(final EntityPlayer par1EntityPlayer, final String par2Str, final float par3, final float par4) {
    }

    @Override
    public void playSoundEffect(final double par1, final double par3, final double par5, final String par7Str, final float par8, final float par9) {
    }

    @Override
    public void playSound(final double par1, final double par3, final double par5, final String par7Str, final float par8, final float par9, final boolean par10) {
    }

    @Override
    public boolean addWeatherEffect(final Entity par1Entity) {
        return false;
    }

    @Override
    public boolean spawnEntityInWorld(final Entity par1Entity) {
        return false;
    }

    @Override
    public void onEntityAdded(final Entity par1Entity) {
    }

    @Override
    public void onEntityRemoved(final Entity par1Entity) {
    }

    @Override
    public void removeEntity(final Entity par1Entity) {
    }

    @Override
    public void removePlayerEntityDangerously(final Entity par1Entity) {
    }

    @Override
    public int calculateSkylightSubtracted(final float par1) {
        return 6;
    }

    @Override
    public void removeWorldAccess(final IWorldAccess par1iWorldAccess) {
    }

    @Override
    public void updateEntities() {
    }

    @Override
    public void updateEntity(final Entity par1Entity) {
    }

    @Override
    public void updateEntityWithOptionalForce(final Entity par1Entity, final boolean par2) {
    }

    @Override
    public boolean checkNoEntityCollision(final AxisAlignedBB par1AxisAlignedBB) {
        return true;
    }

    @Override
    public boolean checkNoEntityCollision(final AxisAlignedBB par1AxisAlignedBB, final Entity par2Entity) {
        return true;
    }

    @Override
    public boolean checkBlockCollision(final AxisAlignedBB par1AxisAlignedBB) {
        return false;
    }

    @Override
    public boolean isAnyLiquid(final AxisAlignedBB par1AxisAlignedBB) {
        return false;
    }

    @Override
    public boolean handleMaterialAcceleration(final AxisAlignedBB par1AxisAlignedBB, final Material par2Material, final Entity par3Entity) {
        return false;
    }

    @Override
    public boolean isMaterialInBB(final AxisAlignedBB par1AxisAlignedBB, final Material par2Material) {
        return false;
    }

    @Override
    public boolean isAABBInMaterial(final AxisAlignedBB par1AxisAlignedBB, final Material par2Material) {
        return false;
    }

    @Override
    public String getDebugLoadedEntities() {
        return "";
    }

    @Override
    public String getProviderName() {
        return "";
    }

    @Override
    public void markTileEntityForRemoval(final TileEntity tileEntityIn) {
    }

    @Override
    public void tick() {
    }

    @Override
    protected void updateWeather() {
    }

    @Override
    protected int getRenderDistanceChunks() {
        return 0;
    }

    @Override
    public List getPendingBlockUpdates(final Chunk par1Chunk, final boolean par2) {
        return null;
    }

    @Override
    public Entity findNearestEntityWithinAABB(final Class par1Class, final AxisAlignedBB par2AxisAlignedBB, final Entity par3Entity) {
        return null;
    }

    @Override
    public int countEntities(final Class par1Class) {
        return 0;
    }

    @Override
    public void checkSessionLock() {
    }

    @Override
    public long getSeed() {
        return 1L;
    }

    @Override
    public long getTotalWorldTime() {
        return 1L;
    }

    @Override
    public long getWorldTime() {
        return 1L;
    }

    @Override
    public void setWorldTime(final long par1) {
    }

    @Override
    public BlockPos getSpawnPoint() {
        return new BlockPos(0, 64, 0);
    }

    @Override
    public void joinEntityInSurroundings(final Entity par1Entity) {
    }

    @Override
    public void setEntityState(final Entity par1Entity, final byte par2) {
    }

    @Override
    public void updateAllPlayersSleepingFlag() {
    }

    @Override
    public void setThunderStrength(final float p_147442_1_) {
    }

    @Override
    public float getRainStrength(final float par1) {
        return 0.0f;
    }

    @Override
    public void setRainStrength(final float par1) {
    }

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public boolean isRaining() {
        return false;
    }

    @Override
    public void setItemData(final String par1Str, final WorldSavedData par2WorldSavedData) {
    }

    @Override
    public int getHeight() {
        return 256;
    }

    @Override
    public int getActualHeight() {
        return 256;
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    @Override
    public void makeFireworks(final double par1,
                              final double par3,
                              final double par5,
                              final double par7,
                              final double par9,
                              final double par11,
                              final NBTTagCompound par13nbtTagCompound) {
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return new FakeChunkProvider();
    }

    @Override
    public Entity getEntityByID(final int i) {
        return EntityList.createEntityByID(i, this);
    }

    @Override
    public Chunk getChunkFromChunkCoords(final int par1, final int par2) {
        return null;
    }

    protected static class FakeWorldProvider extends WorldProvider {

        @Override
        protected void generateLightBrightnessTable() {
        }

        @Override
        public boolean isSurfaceWorld() {
            return true;
        }

        @Override
        public boolean canRespawnHere() {
            return true;
        }

        @Override
        public boolean isSkyColored() {
            return true;
        }

        @Override
        public int getAverageGroundLevel() {
            return 63;
        }

        @Override
        public boolean doesXZShowFog(final int par1, final int par2) {
            return false;
        }

        @Override
        public String getDimensionName() {
            return "";
        }

        @Override
        public String getInternalNameSuffix() {
            return null;
        }

        @Override
        public BlockPos getSpawnCoordinate() {
            return new BlockPos(0, 64, 0);
        }

        @Override
        public boolean canCoordinateBeSpawn(final int par1, final int par2) {
            return true;
        }

    }

    protected static class FakeSaveHandler implements ISaveHandler {

        @Override
        public WorldInfo loadWorldInfo() {
            return null;
        }

        @Override
        public void checkSessionLock() {
        }

        @Override
        public IChunkLoader getChunkLoader(final WorldProvider var1) {
            return null;
        }

        @Override
        public void saveWorldInfoWithPlayer(final WorldInfo var1, final NBTTagCompound var2) {
        }

        @Override
        public void saveWorldInfo(final WorldInfo var1) {
        }

        @Override
        public IPlayerFileData getPlayerNBTManager() {
            return null;
        }

        @Override
        public void flush() {
        }

        @Override
        public File getWorldDirectory() {
            return null;
        }

        @Override
        public File getMapFileFromName(final String var1) {
            return null;
        }

        @Override
        public String getWorldDirectoryName() {
            return null;
        }

    }

    public static class FakeChunkProvider implements IChunkProvider {

        @Override
        public boolean chunkExists(final int var1, final int var2) {
            return false;
        }

        @Override
        public Chunk provideChunk(final int var1, final int var2) {
            return null;
        }

        @Override
        public Chunk provideChunk(BlockPos blockPosIn) {
            return null;
        }

        @Override
        public void populate(final IChunkProvider var1, final int var2, final int var3) {
        }

        @Override
        public boolean populateChunk(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_) {
            return false;
        }

        @Override
        public boolean saveChunks(final boolean var1, final IProgressUpdate var2) {
            return false;
        }

        @Override
        public boolean unloadQueuedChunks() {
            return false;
        }

        @Override
        public boolean canSave() {
            return false;
        }

        @Override
        public String makeString() {
            return null;
        }

        @Override
        public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
            return null;
        }

        @Override
        public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
            return null;
        }

        @Override
        public int getLoadedChunkCount() {
            return 0;
        }

        @Override
        public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_) {
        }

        @Override
        public void saveExtraData() {
        }

    }

}
