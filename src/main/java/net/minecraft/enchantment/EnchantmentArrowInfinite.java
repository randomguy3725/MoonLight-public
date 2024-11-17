package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowInfinite extends Enchantment
{
    public EnchantmentArrowInfinite(int enchID, ResourceLocation enchName, int enchWeight)
    {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.BOW);
        this.setName("arrowInfinite");
    }

    public int getMinEnchantability(int enchantmentLevel)
    {
        return 20;
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return 50;
    }

    public int getMaxLevel()
    {
        return 1;
    }
}
