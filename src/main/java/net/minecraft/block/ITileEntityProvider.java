package net.minecraft.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ITileEntityProvider
{
    TileEntity createNewTileEntity(World worldIn, int meta);
}
