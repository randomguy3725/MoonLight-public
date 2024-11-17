package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentFishingSpeed extends Enchantment
{
    protected EnchantmentFishingSpeed(int enchID, ResourceLocation enchName, int enchWeight, EnumEnchantmentType enchType)
    {
        super(enchID, enchName, enchWeight, enchType);
        this.setName("fishingSpeed");
    }

    public int getMinEnchantability(int enchantmentLevel)
    {
        return 15 + (enchantmentLevel - 1) * 9;
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    public int getMaxLevel()
    {
        return 3;
    }
}
