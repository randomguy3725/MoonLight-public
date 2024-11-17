package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class EnchantmentProtection extends Enchantment
{
    private static final String[] protectionName = new String[] {"all", "fire", "fall", "explosion", "projectile"};
    private static final int[] baseEnchantability = new int[] {1, 10, 5, 5, 3};
    private static final int[] levelEnchantability = new int[] {11, 8, 6, 8, 6};
    private static final int[] thresholdEnchantability = new int[] {20, 12, 10, 12, 15};
    public final int protectionType;

    public EnchantmentProtection(int p_i45765_1_, ResourceLocation p_i45765_2_, int p_i45765_3_, int p_i45765_4_)
    {
        super(p_i45765_1_, p_i45765_2_, p_i45765_3_, EnumEnchantmentType.ARMOR);
        this.protectionType = p_i45765_4_;

        if (p_i45765_4_ == 2)
        {
            this.type = EnumEnchantmentType.ARMOR_FEET;
        }
    }

    public int getMinEnchantability(int enchantmentLevel)
    {
        return baseEnchantability[this.protectionType] + (enchantmentLevel - 1) * levelEnchantability[this.protectionType];
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + thresholdEnchantability[this.protectionType];
    }

    public int getMaxLevel()
    {
        return 4;
    }

    public int calcModifierDamage(int level, DamageSource source)
    {
        if (source.canHarmInCreative())
        {
            return 0;
        }
        else
        {
            float f = (float)(6 + level * level) / 3.0F;
            return this.protectionType == 0 ? MathHelper.floor_float(f * 0.75F) : (this.protectionType == 1 && source.isFireDamage() ? MathHelper.floor_float(f * 1.25F) : (this.protectionType == 2 && source == DamageSource.fall ? MathHelper.floor_float(f * 2.5F) : (this.protectionType == 3 && source.isExplosion() ? MathHelper.floor_float(f * 1.5F) : (this.protectionType == 4 && source.isProjectile() ? MathHelper.floor_float(f * 1.5F) : 0))));
        }
    }

    public String getName()
    {
        return "enchantment.protect." + protectionName[this.protectionType];
    }

    public boolean canApplyTogether(Enchantment ench)
    {
        if (ench instanceof EnchantmentProtection enchantmentprotection)
        {
            return enchantmentprotection.protectionType != this.protectionType && (this.protectionType == 2 || enchantmentprotection.protectionType == 2);
        }
        else
        {
            return super.canApplyTogether(ench);
        }
    }

    public static int getFireTimeForEntity(Entity p_92093_0_, int p_92093_1_)
    {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.fireProtection.effectId, p_92093_0_.getInventory());

        if (i > 0)
        {
            p_92093_1_ -= MathHelper.floor_float((float)p_92093_1_ * (float)i * 0.15F);
        }

        return p_92093_1_;
    }

    public static double func_92092_a(Entity p_92092_0_, double p_92092_1_)
    {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.blastProtection.effectId, p_92092_0_.getInventory());

        if (i > 0)
        {
            p_92092_1_ -= MathHelper.floor_double(p_92092_1_ * (double)((float)i * 0.15F));
        }

        return p_92092_1_;
    }
}
