package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemFood extends Item
{
    public final int itemUseDuration;
    private final int healAmount;
    private final float saturationModifier;
    private final boolean isWolfsFavoriteMeat;
    private boolean alwaysEdible;
    private int potionId;
    private int potionDuration;
    private int potionAmplifier;
    private float potionEffectProbability;

    public ItemFood(int amount, float saturation, boolean isWolfFood)
    {
        this.itemUseDuration = 32;
        this.healAmount = amount;
        this.isWolfsFavoriteMeat = isWolfFood;
        this.saturationModifier = saturation;
        this.setCreativeTab(CreativeTabs.tabFood);
    }

    public ItemFood(int amount, boolean isWolfFood)
    {
        this(amount, 0.6F, isWolfFood);
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        --stack.stackSize;
        playerIn.getFoodStats().addStats(this, stack);
        worldIn.playSoundAtEntity(playerIn, "random.burp", 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
        this.onFoodEaten(stack, worldIn, playerIn);
        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
        return stack;
    }

    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        if (!worldIn.isRemote && this.potionId > 0 && worldIn.rand.nextFloat() < this.potionEffectProbability)
        {
            player.addPotionEffect(new PotionEffect(this.potionId, this.potionDuration * 20, this.potionAmplifier));
        }
    }

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.EAT;
    }

    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        if (playerIn.canEat(this.alwaysEdible))
        {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }

        return itemStackIn;
    }

    public int getHealAmount(ItemStack stack)
    {
        return this.healAmount;
    }

    public float getSaturationModifier(ItemStack stack)
    {
        return this.saturationModifier;
    }

    public boolean isWolfsFavoriteMeat()
    {
        return this.isWolfsFavoriteMeat;
    }

    public ItemFood setPotionEffect(int id, int duration, int amplifier, float probability)
    {
        this.potionId = id;
        this.potionDuration = duration;
        this.potionAmplifier = amplifier;
        this.potionEffectProbability = probability;
        return this;
    }

    public ItemFood setAlwaysEdible()
    {
        this.alwaysEdible = true;
        return this;
    }
}
