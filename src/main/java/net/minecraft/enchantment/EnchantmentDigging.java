package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDigging extends Enchantment
{
    protected EnchantmentDigging(int enchID, ResourceLocation enchName, int enchWeight)
    {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.DIGGER);
        this.setName("digging");
    }

    public int getMinEnchantability(int enchantmentLevel)
    {
        return 1 + 10 * (enchantmentLevel - 1);
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    public int getMaxLevel()
    {
        return 5;
    }

    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() == Items.shears || super.canApply(stack);
    }
}
