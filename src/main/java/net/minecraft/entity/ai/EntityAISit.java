package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAISit extends EntityAIBase
{
    private final EntityTameable theEntity;
    private boolean isSitting;

    public EntityAISit(EntityTameable entityIn)
    {
        this.theEntity = entityIn;
        this.setMutexBits(5);
    }

    public boolean shouldExecute()
    {
        if (!this.theEntity.isTamed())
        {
            return false;
        }
        else if (this.theEntity.isInWater())
        {
            return false;
        }
        else if (!this.theEntity.onGround)
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = this.theEntity.getOwner();
            return entitylivingbase == null || ((!(this.theEntity.getDistanceSqToEntity(entitylivingbase) < 144.0D) || entitylivingbase.getAITarget() == null) && this.isSitting);
        }
    }

    public void startExecuting()
    {
        this.theEntity.getNavigator().clearPathEntity();
        this.theEntity.setSitting(true);
    }

    public void resetTask()
    {
        this.theEntity.setSitting(false);
    }

    public void setSitting(boolean sitting)
    {
        this.isSitting = sitting;
    }
}
