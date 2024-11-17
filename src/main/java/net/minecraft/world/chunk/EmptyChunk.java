package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk
{
    public EmptyChunk(World worldIn, int x, int z)
    {
        super(worldIn, x, z);
    }

    public boolean isAtLocation(int x, int z)
    {
        return x == this.xPosition && z == this.zPosition;
    }

    public int getHeightValue(int x, int z)
    {
        return 0;
    }

    public void generateHeightMap()
    {
    }

    public void generateSkylightMap()
    {
    }

    public Block getBlock(BlockPos pos)
    {
        return Blocks.air;
    }

    public int getBlockLightOpacity(BlockPos pos)
    {
        return 255;
    }

    public int getBlockMetadata(BlockPos pos)
    {
        return 0;
    }

    public int getLightFor(EnumSkyBlock p_177413_1_, BlockPos pos)
    {
        return p_177413_1_.defaultLightValue;
    }

    public void setLightFor(EnumSkyBlock p_177431_1_, BlockPos pos, int value)
    {
    }

    public int getLightSubtracted(BlockPos pos, int amount)
    {
        return 0;
    }

    public void addEntity(Entity entityIn)
    {
    }

    public void removeEntity(Entity entityIn)
    {
    }

    public void removeEntityAtIndex(Entity entityIn, int p_76608_2_)
    {
    }

    public boolean canSeeSky(BlockPos pos)
    {
        return false;
    }

    public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType p_177424_2_)
    {
        return null;
    }

    public void addTileEntity(TileEntity tileEntityIn)
    {
    }

    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn)
    {
    }

    public void removeTileEntity(BlockPos pos)
    {
    }

    public void onChunkLoad()
    {
    }

    public void onChunkUnload()
    {
    }

    public void setChunkModified()
    {
    }

    public void getEntitiesWithinAABBForEntity(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate <? super Entity > p_177414_4_)
    {
    }

    public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class <? extends T > entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate <? super T > p_177430_4_)
    {
    }

    public boolean needsSaving(boolean p_76601_1_)
    {
        return false;
    }

    public Random getRandomWithSeed(long seed)
    {
        return new Random(this.getWorld().getSeed() + ((long) this.xPosition * this.xPosition * 4987142) + (this.xPosition * 5947611L) + (long) this.zPosition * this.zPosition * 4392871L + (this.zPosition * 389711L) ^ seed);
    }

    public boolean isEmpty()
    {
        return true;
    }

    public boolean getAreLevelsEmpty(int startY, int endY)
    {
        return true;
    }
}
