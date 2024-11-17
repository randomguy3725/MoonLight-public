package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

public class ItemSaddle extends Item
{
    public ItemSaddle()
    {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target)
    {
        if (target instanceof EntityPig entitypig)
        {

            if (!entitypig.getSaddled() && !entitypig.isChild())
            {
                entitypig.setSaddled(true);
                entitypig.worldObj.playSoundAtEntity(entitypig, "mob.horse.leather", 0.5F, 1.0F);
                --stack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        this.itemInteractionForEntity(stack, null, target);
        return true;
    }
}
