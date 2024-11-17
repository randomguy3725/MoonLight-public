package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IRecipe
{
    boolean matches(InventoryCrafting inv, World worldIn);

    ItemStack getCraftingResult(InventoryCrafting inv);

    int getRecipeSize();

    ItemStack getRecipeOutput();

    ItemStack[] getRemainingItems(InventoryCrafting inv);
}
