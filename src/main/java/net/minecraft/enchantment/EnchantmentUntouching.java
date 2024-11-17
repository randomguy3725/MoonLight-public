package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentUntouching extends Enchantment
{
    protected EnchantmentUntouching(int p_i45763_1_, ResourceLocation p_i45763_2_, int p_i45763_3_)
    {
        super(p_i45763_1_, p_i45763_2_, p_i45763_3_, EnumEnchantmentType.DIGGER);
        this.setName("untouching");
    }

    public int getMinEnchantability(int enchantmentLevel)
    {
        return 15;
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    public int getMaxLevel()
    {
        return 1;
    }

    public boolean canApplyTogether(Enchantment ench)
    {
        return super.canApplyTogether(ench) && ench.effectId != fortune.effectId;
    }

    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() == Items.shears || super.canApply(stack);
    }
}
