package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockBookshelf extends Block
{
    public BlockBookshelf()
    {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public int quantityDropped(Random random)
    {
        return 3;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.book;
    }
}
