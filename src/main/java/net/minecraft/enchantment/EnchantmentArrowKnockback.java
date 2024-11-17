package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowKnockback extends Enchantment
{
    public EnchantmentArrowKnockback(int enchID, ResourceLocation enchName, int enchWeight)
    {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.BOW);
        this.setName("arrowKnockback");
    }

    public int getMinEnchantability(int enchantmentLevel)
    {
        return 12 + (enchantmentLevel - 1) * 20;
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 25;
    }

    public int getMaxLevel()
    {
        return 2;
    }
}
