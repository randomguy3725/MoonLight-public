package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockMelon extends Block
{
    protected BlockMelon()
    {
        super(Material.gourd, MapColor.limeColor);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.melon;
    }

    public int quantityDropped(Random random)
    {
        return 3 + random.nextInt(5);
    }

    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return Math.min(9, this.quantityDropped(random) + random.nextInt(1 + fortune));
    }
}
