package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;

public class EntityAIFollowGolem extends EntityAIBase
{
    private final EntityVillager theVillager;
    private EntityIronGolem theGolem;
    private int takeGolemRoseTick;
    private boolean tookGolemRose;

    public EntityAIFollowGolem(EntityVillager theVillagerIn)
    {
        this.theVillager = theVillagerIn;
        this.setMutexBits(3);
    }

    public boolean shouldExecute()
    {
        if (this.theVillager.getGrowingAge() >= 0)
        {
            return false;
        }
        else if (!this.theVillager.worldObj.isDaytime())
        {
            return false;
        }
        else
        {
            List<EntityIronGolem> list = this.theVillager.worldObj.getEntitiesWithinAABB(EntityIronGolem.class, this.theVillager.getEntityBoundingBox().expand(6.0D, 2.0D, 6.0D));

            if (list.isEmpty())
            {
                return false;
            }
            else
            {
                for (EntityIronGolem entityirongolem : list)
                {
                    if (entityirongolem.getHoldRoseTick() > 0)
                    {
                        this.theGolem = entityirongolem;
                        break;
                    }
                }

                return this.theGolem != null;
            }
        }
    }

    public boolean continueExecuting()
    {
        return this.theGolem.getHoldRoseTick() > 0;
    }

    public void startExecuting()
    {
        this.takeGolemRoseTick = this.theVillager.getRNG().nextInt(320);
        this.tookGolemRose = false;
        this.theGolem.getNavigator().clearPathEntity();
    }

    public void resetTask()
    {
        this.theGolem = null;
        this.theVillager.getNavigator().clearPathEntity();
    }

    public void updateTask()
    {
        this.theVillager.getLookHelper().setLookPositionWithEntity(this.theGolem, 30.0F, 30.0F);

        if (this.theGolem.getHoldRoseTick() == this.takeGolemRoseTick)
        {
            this.theVillager.getNavigator().tryMoveToEntityLiving(this.theGolem, 0.5D);
            this.tookGolemRose = true;
        }

        if (this.tookGolemRose && this.theVillager.getDistanceSqToEntity(this.theGolem) < 4.0D)
        {
            this.theGolem.setHoldingRose(false);
            this.theVillager.getNavigator().clearPathEntity();
        }
    }
}
