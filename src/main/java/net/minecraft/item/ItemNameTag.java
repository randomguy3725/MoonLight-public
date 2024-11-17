package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ItemNameTag extends Item
{
    public ItemNameTag()
    {
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target)
    {
        if (!stack.hasDisplayName())
        {
            return false;
        }
        else if (target instanceof EntityLiving entityliving)
        {
            entityliving.setCustomNameTag(stack.getDisplayName());
            entityliving.enablePersistence();
            --stack.stackSize;
            return true;
        }
        else
        {
            return super.itemInteractionForEntity(stack, playerIn, target);
        }
    }
}
