package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart
{
    public EntityMinecartEmpty(World worldIn)
    {
        super(worldIn);
    }

    public EntityMinecartEmpty(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    public boolean interactFirst(EntityPlayer playerIn)
    {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != playerIn)
        {
            return true;
        }
        else if (this.riddenByEntity != null && this.riddenByEntity != playerIn)
        {
            return false;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                playerIn.mountEntity(this);
            }

            return true;
        }
    }

    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
    {
        if (receivingPower)
        {
            if (this.riddenByEntity != null)
            {
                this.riddenByEntity.mountEntity(null);
            }

            if (this.getRollingAmplitude() == 0)
            {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setDamage(50.0F);
                this.setBeenAttacked();
            }
        }
    }

    public EntityMinecart.EnumMinecartType getMinecartType()
    {
        return EntityMinecart.EnumMinecartType.RIDEABLE;
    }
}
