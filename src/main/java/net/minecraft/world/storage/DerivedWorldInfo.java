package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class DerivedWorldInfo extends WorldInfo
{
    private final WorldInfo theWorldInfo;

    public DerivedWorldInfo(WorldInfo p_i2145_1_)
    {
        this.theWorldInfo = p_i2145_1_;
    }

    public NBTTagCompound getNBTTagCompound()
    {
        return this.theWorldInfo.getNBTTagCompound();
    }

    public NBTTagCompound cloneNBTCompound(NBTTagCompound nbt)
    {
        return this.theWorldInfo.cloneNBTCompound(nbt);
    }

    public long getSeed()
    {
        return this.theWorldInfo.getSeed();
    }

    public int getSpawnX()
    {
        return this.theWorldInfo.getSpawnX();
    }

    public int getSpawnY()
    {
        return this.theWorldInfo.getSpawnY();
    }

    public int getSpawnZ()
    {
        return this.theWorldInfo.getSpawnZ();
    }

    public long getWorldTotalTime()
    {
        return this.theWorldInfo.getWorldTotalTime();
    }

    public long getWorldTime()
    {
        return this.theWorldInfo.getWorldTime();
    }

    public long getSizeOnDisk()
    {
        return this.theWorldInfo.getSizeOnDisk();
    }

    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return this.theWorldInfo.getPlayerNBTTagCompound();
    }

    public String getWorldName()
    {
        return this.theWorldInfo.getWorldName();
    }

    public int getSaveVersion()
    {
        return this.theWorldInfo.getSaveVersion();
    }

    public long getLastTimePlayed()
    {
        return this.theWorldInfo.getLastTimePlayed();
    }

    public boolean isThundering()
    {
        return this.theWorldInfo.isThundering();
    }

    public int getThunderTime()
    {
        return this.theWorldInfo.getThunderTime();
    }

    public boolean isRaining()
    {
        return this.theWorldInfo.isRaining();
    }

    public int getRainTime()
    {
        return this.theWorldInfo.getRainTime();
    }

    public WorldSettings.GameType getGameType()
    {
        return this.theWorldInfo.getGameType();
    }

    public void setSpawnX(int x)
    {
    }

    public void setSpawnY(int y)
    {
    }

    public void setSpawnZ(int z)
    {
    }

    public void setWorldTotalTime(long time)
    {
    }

    public void setWorldTime(long time)
    {
    }

    public void setSpawn(BlockPos spawnPoint)
    {
    }

    public void setWorldName(String worldName)
    {
    }

    public void setSaveVersion(int version)
    {
    }

    public void setThundering(boolean thunderingIn)
    {
    }

    public void setThunderTime(int time)
    {
    }

    public void setRaining(boolean isRaining)
    {
    }

    public void setRainTime(int time)
    {
    }

    public boolean isMapFeaturesEnabled()
    {
        return this.theWorldInfo.isMapFeaturesEnabled();
    }

    public boolean isHardcoreModeEnabled()
    {
        return this.theWorldInfo.isHardcoreModeEnabled();
    }

    public WorldType getTerrainType()
    {
        return this.theWorldInfo.getTerrainType();
    }

    public void setTerrainType(WorldType type)
    {
    }

    public boolean areCommandsAllowed()
    {
        return this.theWorldInfo.areCommandsAllowed();
    }

    public void setAllowCommands(boolean allow)
    {
    }

    public boolean isInitialized()
    {
        return this.theWorldInfo.isInitialized();
    }

    public void setServerInitialized(boolean initializedIn)
    {
    }

    public GameRules getGameRulesInstance()
    {
        return this.theWorldInfo.getGameRulesInstance();
    }

    public EnumDifficulty getDifficulty()
    {
        return this.theWorldInfo.getDifficulty();
    }

    public void setDifficulty(EnumDifficulty newDifficulty)
    {
    }

    public boolean isDifficultyLocked()
    {
        return this.theWorldInfo.isDifficultyLocked();
    }

    public void setDifficultyLocked(boolean locked)
    {
    }
}
