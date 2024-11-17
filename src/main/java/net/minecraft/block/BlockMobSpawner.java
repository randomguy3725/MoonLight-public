package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockMobSpawner extends BlockContainer
{
    protected BlockMobSpawner()
    {
        super(Material.rock);
    }

    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityMobSpawner();
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    public int quantityDropped(Random random)
    {
        return 0;
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        int i = 15 + worldIn.rand.nextInt(15) + worldIn.rand.nextInt(15);
        this.dropXpOnBlockBreak(worldIn, pos, i);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public int getRenderType()
    {
        return 3;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return null;
    }
}
