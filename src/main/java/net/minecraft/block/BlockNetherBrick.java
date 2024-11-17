package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;

public class BlockNetherBrick extends Block
{
    public BlockNetherBrick()
    {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public MapColor getMapColor(IBlockState state)
    {
        return MapColor.netherrackColor;
    }
}
