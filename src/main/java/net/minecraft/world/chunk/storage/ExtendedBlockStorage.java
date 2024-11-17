package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;
import net.optifine.reflect.Reflector;

public class ExtendedBlockStorage
{
    private final int yBase;
    private int blockRefCount;
    private int tickRefCount;
    private char[] data;
    private NibbleArray blocklightArray;
    private NibbleArray skylightArray;

    public ExtendedBlockStorage(int y, boolean storeSkylight)
    {
        this.yBase = y;
        this.data = new char[4096];
        this.blocklightArray = new NibbleArray();

        if (storeSkylight)
        {
            this.skylightArray = new NibbleArray();
        }
    }

    public IBlockState get(int x, int y, int z)
    {
        IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(this.data[y << 8 | z << 4 | x]);
        return iblockstate != null ? iblockstate : Blocks.air.getDefaultState();
    }

    public void set(int x, int y, int z, IBlockState state)
    {
        if (Reflector.IExtendedBlockState.isInstance(state))
        {
            state = (IBlockState)Reflector.call(state, Reflector.IExtendedBlockState_getClean, new Object[0]);
        }

        IBlockState iblockstate = this.get(x, y, z);
        Block block = iblockstate.getBlock();
        Block block1 = state.getBlock();

        if (block != Blocks.air)
        {
            --this.blockRefCount;

            if (block.getTickRandomly())
            {
                --this.tickRefCount;
            }
        }

        if (block1 != Blocks.air)
        {
            ++this.blockRefCount;

            if (block1.getTickRandomly())
            {
                ++this.tickRefCount;
            }
        }

        this.data[y << 8 | z << 4 | x] = (char)Block.BLOCK_STATE_IDS.get(state);
    }

    public Block getBlockByExtId(int x, int y, int z)
    {
        return this.get(x, y, z).getBlock();
    }

    public int getExtBlockMetadata(int x, int y, int z)
    {
        IBlockState iblockstate = this.get(x, y, z);
        return iblockstate.getBlock().getMetaFromState(iblockstate);
    }

    public boolean isEmpty()
    {
        return this.blockRefCount == 0;
    }

    public boolean getNeedsRandomTick()
    {
        return this.tickRefCount > 0;
    }

    public int getYLocation()
    {
        return this.yBase;
    }

    public void setExtSkylightValue(int x, int y, int z, int value)
    {
        this.skylightArray.set(x, y, z, value);
    }

    public int getExtSkylightValue(int x, int y, int z)
    {
        return this.skylightArray.get(x, y, z);
    }

    public void setExtBlocklightValue(int x, int y, int z, int value)
    {
        this.blocklightArray.set(x, y, z, value);
    }

    public int getExtBlocklightValue(int x, int y, int z)
    {
        return this.blocklightArray.get(x, y, z);
    }

    public void removeInvalidBlocks()
    {
        IBlockState iblockstate = Blocks.air.getDefaultState();
        int i = 0;
        int j = 0;

        for (int k = 0; k < 16; ++k)
        {
            for (int l = 0; l < 16; ++l)
            {
                for (int i1 = 0; i1 < 16; ++i1)
                {
                    Block block = this.getBlockByExtId(i1, k, l);

                    if (block != Blocks.air)
                    {
                        ++i;

                        if (block.getTickRandomly())
                        {
                            ++j;
                        }
                    }
                }
            }
        }

        this.blockRefCount = i;
        this.tickRefCount = j;
    }

    public char[] getData()
    {
        return this.data;
    }

    public void setData(char[] dataArray)
    {
        this.data = dataArray;
    }

    public NibbleArray getBlocklightArray()
    {
        return this.blocklightArray;
    }

    public NibbleArray getSkylightArray()
    {
        return this.skylightArray;
    }

    public void setBlocklightArray(NibbleArray newBlocklightArray)
    {
        this.blocklightArray = newBlocklightArray;
    }

    public void setSkylightArray(NibbleArray newSkylightArray)
    {
        this.skylightArray = newSkylightArray;
    }

    public int getBlockRefCount()
    {
        return this.blockRefCount;
    }
}
