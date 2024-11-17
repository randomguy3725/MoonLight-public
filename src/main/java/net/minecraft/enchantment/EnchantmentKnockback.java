package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentKnockback extends Enchantment
{
    protected EnchantmentKnockback(int p_i45768_1_, ResourceLocation p_i45768_2_, int p_i45768_3_)
    {
        super(p_i45768_1_, p_i45768_2_, p_i45768_3_, EnumEnchantmentType.WEAPON);
        this.setName("knockback");
    }

    public int getMinEnchantability(int enchantmentLevel)
    {
        return 5 + 20 * (enchantmentLevel - 1);
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
