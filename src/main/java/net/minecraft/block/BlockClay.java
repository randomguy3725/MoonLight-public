package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockClay extends Block
{
    public BlockClay()
    {
        super(Material.clay);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.clay_ball;
    }

    public int quantityDropped(Random random)
    {
        return 4;
    }
}
