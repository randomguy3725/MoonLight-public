package net.optifine;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public enum BlockDir
{
    DOWN(EnumFacing.DOWN),
    UP(EnumFacing.UP),
    NORTH(EnumFacing.NORTH),
    SOUTH(EnumFacing.SOUTH),
    WEST(EnumFacing.WEST),
    EAST(EnumFacing.EAST),
    NORTH_WEST(EnumFacing.NORTH, EnumFacing.WEST),
    NORTH_EAST(EnumFacing.NORTH, EnumFacing.EAST),
    SOUTH_WEST(EnumFacing.SOUTH, EnumFacing.WEST),
    SOUTH_EAST(EnumFacing.SOUTH, EnumFacing.EAST),
    DOWN_NORTH(EnumFacing.DOWN, EnumFacing.NORTH),
    DOWN_SOUTH(EnumFacing.DOWN, EnumFacing.SOUTH),
    UP_NORTH(EnumFacing.UP, EnumFacing.NORTH),
    UP_SOUTH(EnumFacing.UP, EnumFacing.SOUTH),
    DOWN_WEST(EnumFacing.DOWN, EnumFacing.WEST),
    DOWN_EAST(EnumFacing.DOWN, EnumFacing.EAST),
    UP_WEST(EnumFacing.UP, EnumFacing.WEST),
    UP_EAST(EnumFacing.UP, EnumFacing.EAST);

    private final EnumFacing facing1;
    private EnumFacing facing2;

    BlockDir(EnumFacing facing1)
    {
        this.facing1 = facing1;
    }

    BlockDir(EnumFacing facing1, EnumFacing facing2)
    {
        this.facing1 = facing1;
        this.facing2 = facing2;
    }

    public EnumFacing getFacing1()
    {
        return this.facing1;
    }

    public EnumFacing getFacing2()
    {
        return this.facing2;
    }

    BlockPos offset(BlockPos pos)
    {
        pos = pos.offset(this.facing1, 1);

        if (this.facing2 != null)
        {
            pos = pos.offset(this.facing2, 1);
        }

        return pos;
    }

    public int getOffsetX()
    {
        int i = this.facing1.getFrontOffsetX();

        if (this.facing2 != null)
        {
            i += this.facing2.getFrontOffsetX();
        }

        return i;
    }

    public int getOffsetY()
    {
        int i = this.facing1.getFrontOffsetY();

        if (this.facing2 != null)
        {
            i += this.facing2.getFrontOffsetY();
        }

        return i;
    }

    public int getOffsetZ()
    {
        int i = this.facing1.getFrontOffsetZ();

        if (this.facing2 != null)
        {
            i += this.facing2.getFrontOffsetZ();
        }

        return i;
    }

    public boolean isDouble()
    {
        return this.facing2 != null;
    }
}
