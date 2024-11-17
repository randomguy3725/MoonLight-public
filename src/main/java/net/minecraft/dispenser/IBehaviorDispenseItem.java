package net.minecraft.dispenser;

import net.minecraft.item.ItemStack;

public interface IBehaviorDispenseItem
{
    IBehaviorDispenseItem itemDispenseBehaviorProvider = new IBehaviorDispenseItem()
    {
        public ItemStack dispense(IBlockSource source, ItemStack stack)
        {
            return stack;
        }
    };

    ItemStack dispense(IBlockSource source, ItemStack stack);
}
