package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public class BlockSeaLantern extends Block
{
    public BlockSeaLantern(Material materialIn)
    {
        super(materialIn);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public int quantityDropped(Random random)
    {
        return 2 + random.nextInt(2);
    }

    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return MathHelper.clamp_int(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 5);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.prismarine_crystals;
    }

    public MapColor getMapColor(IBlockState state)
    {
        return MapColor.quartzColor;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }
}
