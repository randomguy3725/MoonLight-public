package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemPiston extends ItemBlock
{
    public ItemPiston(Block block)
    {
        super(block);
    }

    public int getMetadata(int damage)
    {
        return 7;
    }
}
