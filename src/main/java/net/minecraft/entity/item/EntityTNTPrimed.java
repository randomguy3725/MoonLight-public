package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityTNTPrimed extends Entity
{
    public int fuse;
    private EntityLivingBase tntPlacedBy;

    public EntityTNTPrimed(World worldIn)
    {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
    }

    public EntityTNTPrimed(World worldIn, double x, double y, double z, EntityLivingBase igniter)
    {
        this(worldIn);
        this.setPosition(x, y, z);
        float f = (float)(Math.random() * Math.PI * 2.0D);
        this.motionX = -((float)Math.sin(f)) * 0.02F;
        this.motionY = 0.20000000298023224D;
        this.motionZ = -((float)Math.cos(f)) * 0.02F;
        this.fuse = 80;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.tntPlacedBy = igniter;
    }

    protected void entityInit()
    {
    }

    protected boolean canTriggerWalking()
    {
        return false;
    }

    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;
        }

        if (this.fuse-- <= 0)
        {
            this.setDead();

            if (!this.worldObj.isRemote)
            {
                this.explode();
            }
        }
        else
        {
            this.handleWaterMovement();
            this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void explode()
    {
        float f = 4.0F;
        this.worldObj.createExplosion(this, this.posX, this.posY + (double)(this.height / 16.0F), this.posZ, f, true);
    }

    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setByte("Fuse", (byte)this.fuse);
    }

    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        this.fuse = tagCompund.getByte("Fuse");
    }

    public EntityLivingBase getTntPlacedBy()
    {
        return this.tntPlacedBy;
    }

    public float getEyeHeight()
    {
        return 0.0F;
    }
}
