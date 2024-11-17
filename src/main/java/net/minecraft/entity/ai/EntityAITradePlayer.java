package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class EntityAITradePlayer extends EntityAIBase
{
    private final EntityVillager villager;

    public EntityAITradePlayer(EntityVillager villagerIn)
    {
        this.villager = villagerIn;
        this.setMutexBits(5);
    }

    public boolean shouldExecute()
    {
        if (!this.villager.isEntityAlive())
        {
            return false;
        }
        else if (this.villager.isInWater())
        {
            return false;
        }
        else if (!this.villager.onGround)
        {
            return false;
        }
        else if (this.villager.velocityChanged)
        {
            return false;
        }
        else
        {
            EntityPlayer entityplayer = this.villager.getCustomer();
            return entityplayer != null && (!(this.villager.getDistanceSqToEntity(entityplayer) > 16.0D) && entityplayer.openContainer instanceof Container);
        }
    }

    public void startExecuting()
    {
        this.villager.getNavigator().clearPathEntity();
    }

    public void resetTask()
    {
        this.villager.setCustomer(null);
    }
}
