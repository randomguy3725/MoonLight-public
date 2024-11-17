package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface ISidedInventory extends IInventory
{
    int[] getSlotsForFace(EnumFacing side);

    boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction);

    boolean canExtractItem(int index, ItemStack stack, EnumFacing direction);
}
