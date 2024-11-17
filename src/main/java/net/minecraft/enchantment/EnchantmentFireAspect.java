package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentFireAspect extends Enchantment
{
    protected EnchantmentFireAspect(int enchID, ResourceLocation enchName, int enchWeight)
    {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.WEAPON);
        this.setName("fire");
    }

    public int getMinEnchantability(int enchantmentLevel)
    {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    public int getMaxLevel()
    {
        return 2;
    }
}
