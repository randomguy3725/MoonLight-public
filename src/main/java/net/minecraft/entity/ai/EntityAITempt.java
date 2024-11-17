package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAITempt extends EntityAIBase
{
    private final EntityCreature temptedEntity;
    private final double speed;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double pitch;
    private double yaw;
    private EntityPlayer temptingPlayer;
    private int delayTemptCounter;
    private boolean isRunning;
    private final Item temptItem;
    private final boolean scaredByPlayerMovement;
    private boolean avoidWater;

    public EntityAITempt(EntityCreature temptedEntityIn, double speedIn, Item temptItemIn, boolean scaredByPlayerMovementIn)
    {
        this.temptedEntity = temptedEntityIn;
        this.speed = speedIn;
        this.temptItem = temptItemIn;
        this.scaredByPlayerMovement = scaredByPlayerMovementIn;
        this.setMutexBits(3);

        if (!(temptedEntityIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    public boolean shouldExecute()
    {
        if (this.delayTemptCounter > 0)
        {
            --this.delayTemptCounter;
            return false;
        }
        else
        {
            this.temptingPlayer = this.temptedEntity.worldObj.getClosestPlayerToEntity(this.temptedEntity, 10.0D);

            if (this.temptingPlayer == null)
            {
                return false;
            }
            else
            {
                ItemStack itemstack = this.temptingPlayer.getCurrentEquippedItem();
                return itemstack != null && itemstack.getItem() == this.temptItem;
            }
        }
    }

    public boolean continueExecuting()
    {
        if (this.scaredByPlayerMovement)
        {
            if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 36.0D)
            {
                if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D)
                {
                    return false;
                }

                if (Math.abs((double)this.temptingPlayer.rotationPitch - this.pitch) > 5.0D || Math.abs((double)this.temptingPlayer.rotationYaw - this.yaw) > 5.0D)
                {
                    return false;
                }
            }
            else
            {
                this.targetX = this.temptingPlayer.posX;
                this.targetY = this.temptingPlayer.posY;
                this.targetZ = this.temptingPlayer.posZ;
            }

            this.pitch = this.temptingPlayer.rotationPitch;
            this.yaw = this.temptingPlayer.rotationYaw;
        }

        return this.shouldExecute();
    }

    public void startExecuting()
    {
        this.targetX = this.temptingPlayer.posX;
        this.targetY = this.temptingPlayer.posY;
        this.targetZ = this.temptingPlayer.posZ;
        this.isRunning = true;
        this.avoidWater = ((PathNavigateGround)this.temptedEntity.getNavigator()).getAvoidsWater();
        ((PathNavigateGround)this.temptedEntity.getNavigator()).setAvoidsWater(false);
    }

    public void resetTask()
    {
        this.temptingPlayer = null;
        this.temptedEntity.getNavigator().clearPathEntity();
        this.delayTemptCounter = 100;
        this.isRunning = false;
        ((PathNavigateGround)this.temptedEntity.getNavigator()).setAvoidsWater(this.avoidWater);
    }

    public void updateTask()
    {
        this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, 30.0F, (float)this.temptedEntity.getVerticalFaceSpeed());

        if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 6.25D)
        {
            this.temptedEntity.getNavigator().clearPathEntity();
        }
        else
        {
            this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.speed);
        }
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }
}
