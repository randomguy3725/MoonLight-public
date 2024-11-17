package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemAnvilBlock extends ItemMultiTexture
{
    public ItemAnvilBlock(Block block)
    {
        super(block, block, new String[] {"intact", "slightlyDamaged", "veryDamaged"});
    }

    public int getMetadata(int damage)
    {
        return damage << 2;
    }
}
