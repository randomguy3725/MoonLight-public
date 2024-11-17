package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDamage extends Enchantment
{
    private static final String[] protectionName = new String[] {"all", "undead", "arthropods"};
    private static final int[] baseEnchantability = new int[] {1, 5, 5};
    private static final int[] levelEnchantability = new int[] {11, 8, 8};
    private static final int[] thresholdEnchantability = new int[] {20, 20, 20};
    public final int damageType;

    public EnchantmentDamage(int enchID, ResourceLocation enchName, int enchWeight, int classification)
    {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.WEAPON);
        this.damageType = classification;
    }

    public int getMinEnchantability(int enchantmentLevel)
    {
        return baseEnchantability[this.damageType] + (enchantmentLevel - 1) * levelEnchantability[this.damageType];
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + thresholdEnchantability[this.damageType];
    }

    public int getMaxLevel()
    {
        return 5;
    }

    public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
    {
        return this.damageType == 0 ? (float)level * 1.25F : (this.damageType == 1 && creatureType == EnumCreatureAttribute.UNDEAD ? (float)level * 2.5F : (this.damageType == 2 && creatureType == EnumCreatureAttribute.ARTHROPOD ? (float)level * 2.5F : 0.0F));
    }

    public String getName()
    {
        return "enchantment.damage." + protectionName[this.damageType];
    }

    public boolean canApplyTogether(Enchantment ench)
    {
        return !(ench instanceof EnchantmentDamage);
    }

    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() instanceof ItemAxe || super.canApply(stack);
    }

    public void onEntityDamaged(EntityLivingBase user, Entity target, int level)
    {
        if (target instanceof EntityLivingBase entitylivingbase)
        {

            if (this.damageType == 2 && entitylivingbase.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD)
            {
                int i = 20 + user.getRNG().nextInt(10 * level);
                entitylivingbase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, i, 3));
            }
        }
    }
}
