package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchClosest extends EntityAIBase
{
    protected EntityLiving theWatcher;
    protected Entity closestEntity;
    protected float maxDistanceForPlayer;
    private int lookTime;
    private final float chance;
    protected Class <? extends Entity > watchedClass;

    public EntityAIWatchClosest(EntityLiving entitylivingIn, Class <? extends Entity > watchTargetClass, float maxDistance)
    {
        this.theWatcher = entitylivingIn;
        this.watchedClass = watchTargetClass;
        this.maxDistanceForPlayer = maxDistance;
        this.chance = 0.02F;
        this.setMutexBits(2);
    }

    public EntityAIWatchClosest(EntityLiving entitylivingIn, Class <? extends Entity > watchTargetClass, float maxDistance, float chanceIn)
    {
        this.theWatcher = entitylivingIn;
        this.watchedClass = watchTargetClass;
        this.maxDistanceForPlayer = maxDistance;
        this.chance = chanceIn;
        this.setMutexBits(2);
    }

    public boolean shouldExecute()
    {
        if (this.theWatcher.getRNG().nextFloat() >= this.chance)
        {
            return false;
        }
        else
        {
            if (this.theWatcher.getAttackTarget() != null)
            {
                this.closestEntity = this.theWatcher.getAttackTarget();
            }

            if (this.watchedClass == EntityPlayer.class)
            {
                this.closestEntity = this.theWatcher.worldObj.getClosestPlayerToEntity(this.theWatcher, this.maxDistanceForPlayer);
            }
            else
            {
                this.closestEntity = this.theWatcher.worldObj.findNearestEntityWithinAABB(this.watchedClass, this.theWatcher.getEntityBoundingBox().expand(this.maxDistanceForPlayer, 3.0D, this.maxDistanceForPlayer), this.theWatcher);
            }

            return this.closestEntity != null;
        }
    }

    public boolean continueExecuting()
    {
        return this.closestEntity.isEntityAlive() && (!(this.theWatcher.getDistanceSqToEntity(this.closestEntity) > (double) (this.maxDistanceForPlayer * this.maxDistanceForPlayer)) && this.lookTime > 0);
    }

    public void startExecuting()
    {
        this.lookTime = 40 + this.theWatcher.getRNG().nextInt(40);
    }

    public void resetTask()
    {
        this.closestEntity = null;
    }

    public void updateTask()
    {
        this.theWatcher.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double)this.closestEntity.getEyeHeight(), this.closestEntity.posZ, 10.0F, (float)this.theWatcher.getVerticalFaceSpeed());
        --this.lookTime;
    }
}
