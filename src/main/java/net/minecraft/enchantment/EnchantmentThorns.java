package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EnchantmentThorns extends Enchantment
{
    public EnchantmentThorns(int p_i45764_1_, ResourceLocation p_i45764_2_, int p_i45764_3_)
    {
        super(p_i45764_1_, p_i45764_2_, p_i45764_3_, EnumEnchantmentType.ARMOR_TORSO);
        this.setName("thorns");
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
        return 3;
    }

    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() instanceof ItemArmor || super.canApply(stack);
    }

    public void onUserHurt(EntityLivingBase user, Entity attacker, int level)
    {
        Random random = user.getRNG();
        ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Enchantment.thorns, user);

        if (func_92094_a(level, random))
        {
            if (attacker != null)
            {
                attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), (float)func_92095_b(level, random));
                attacker.playSound("damage.thorns", 0.5F, 1.0F);
            }

            if (itemstack != null)
            {
                itemstack.damageItem(3, user);
            }
        }
        else if (itemstack != null)
        {
            itemstack.damageItem(1, user);
        }
    }

    public static boolean func_92094_a(int p_92094_0_, Random p_92094_1_)
    {
        return p_92094_0_ > 0 && p_92094_1_.nextFloat() < 0.15F * (float) p_92094_0_;
    }

    public static int func_92095_b(int p_92095_0_, Random p_92095_1_)
    {
        return p_92095_0_ > 10 ? p_92095_0_ - 10 : 1 + p_92095_1_.nextInt(4);
    }
}
