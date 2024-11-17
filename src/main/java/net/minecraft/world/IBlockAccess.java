package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;

public interface IBlockAccess
{
    TileEntity getTileEntity(BlockPos pos);

    int getCombinedLight(BlockPos pos, int lightValue);

    IBlockState getBlockState(BlockPos pos);

    boolean isAirBlock(BlockPos pos);

    BiomeGenBase getBiomeGenForCoords(BlockPos pos);

    boolean extendedLevelsInChunkCache();

    int getStrongPower(BlockPos pos, EnumFacing direction);

    WorldType getWorldType();
}
