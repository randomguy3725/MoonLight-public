package net.minecraft.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityDragonPart extends Entity
{
    public final IEntityMultiPart entityDragonObj;
    public final String partName;

    public EntityDragonPart(IEntityMultiPart parent, String partName, float base, float sizeHeight)
    {
        super(parent.getWorld());
        this.setSize(base, sizeHeight);
        this.entityDragonObj = parent;
        this.partName = partName;
    }

    protected void entityInit()
    {
    }

    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
    }

    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
    }

    public boolean canBeCollidedWith()
    {
        return true;
    }

    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return !this.isEntityInvulnerable(source) && this.entityDragonObj.attackEntityFromPart(this, source, amount);
    }

    public boolean isEntityEqual(Entity entityIn)
    {
        return this == entityIn || this.entityDragonObj == entityIn;
    }
}
