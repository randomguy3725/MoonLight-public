package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockBarrier extends Block
{
    protected BlockBarrier()
    {
        super(Material.barrier);
        this.setBlockUnbreakable();
        this.setResistance(6000001.0F);
        this.disableStats();
        this.translucent = true;
    }

    public int getRenderType()
    {
        return -1;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public float getAmbientOcclusionLightValue()
    {
        return 1.0F;
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
    }
}
