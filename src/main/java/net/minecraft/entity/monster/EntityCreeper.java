package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCreeper extends EntityMob
{
    private int lastActiveTime;
    private int timeSinceIgnited;
    private int fuseTime = 30;
    private int explosionRadius = 3;
    private int field_175494_bm = 0;

    public EntityCreeper(World worldIn)
    {
        super(worldIn);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAICreeperSwell(this));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, false));
        this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    public int getMaxFallHeight()
    {
        return this.getAttackTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
    }

    public void fall(float distance, float damageMultiplier)
    {
        super.fall(distance, damageMultiplier);
        this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + distance * 1.5F);

        if (this.timeSinceIgnited > this.fuseTime - 5)
        {
            this.timeSinceIgnited = this.fuseTime - 5;
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte) - 1));
        this.dataWatcher.addObject(17, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(18, Byte.valueOf((byte)0));
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);

        if (this.dataWatcher.getWatchableObjectByte(17) == 1)
        {
            tagCompound.setBoolean("powered", true);
        }

        tagCompound.setShort("Fuse", (short)this.fuseTime);
        tagCompound.setByte("ExplosionRadius", (byte)this.explosionRadius);
        tagCompound.setBoolean("ignited", this.hasIgnited());
    }

    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        this.dataWatcher.updateObject(17, Byte.valueOf((byte)(tagCompund.getBoolean("powered") ? 1 : 0)));

        if (tagCompund.hasKey("Fuse", 99))
        {
            this.fuseTime = tagCompund.getShort("Fuse");
        }

        if (tagCompund.hasKey("ExplosionRadius", 99))
        {
            this.explosionRadius = tagCompund.getByte("ExplosionRadius");
        }

        if (tagCompund.getBoolean("ignited"))
        {
            this.ignite();
        }
    }

    public void onUpdate()
    {
        if (this.isEntityAlive())
        {
            this.lastActiveTime = this.timeSinceIgnited;

            if (this.hasIgnited())
            {
                this.setCreeperState(1);
            }

            int i = this.getCreeperState();

            if (i > 0 && this.timeSinceIgnited == 0)
            {
                this.playSound("creeper.primed", 1.0F, 0.5F);
            }

            this.timeSinceIgnited += i;

            if (this.timeSinceIgnited < 0)
            {
                this.timeSinceIgnited = 0;
            }

            if (this.timeSinceIgnited >= this.fuseTime)
            {
                this.timeSinceIgnited = this.fuseTime;
                this.explode();
            }
        }

        super.onUpdate();
    }

    protected String getHurtSound()
    {
        return "mob.creeper.say";
    }

    protected String getDeathSound()
    {
        return "mob.creeper.death";
    }

    public void onDeath(DamageSource cause)
    {
        super.onDeath(cause);

        if (cause.getEntity() instanceof EntitySkeleton)
        {
            int i = Item.getIdFromItem(Items.record_13);
            int j = Item.getIdFromItem(Items.record_wait);
            int k = i + this.rand.nextInt(j - i + 1);
            this.dropItem(Item.getItemById(k), 1);
        }
        else if (cause.getEntity() instanceof EntityCreeper && cause.getEntity() != this && ((EntityCreeper)cause.getEntity()).getPowered() && ((EntityCreeper)cause.getEntity()).isAIEnabled())
        {
            ((EntityCreeper)cause.getEntity()).func_175493_co();
            this.entityDropItem(new ItemStack(Items.skull, 1, 4), 0.0F);
        }
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        return true;
    }

    public boolean getPowered()
    {
        return this.dataWatcher.getWatchableObjectByte(17) == 1;
    }

    public float getCreeperFlashIntensity(float p_70831_1_)
    {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_) / (float)(this.fuseTime - 2);
    }

    protected Item getDropItem()
    {
        return Items.gunpowder;
    }

    public int getCreeperState()
    {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    public void setCreeperState(int state)
    {
        this.dataWatcher.updateObject(16, Byte.valueOf((byte)state));
    }

    public void onStruckByLightning(EntityLightningBolt lightningBolt)
    {
        super.onStruckByLightning(lightningBolt);
        this.dataWatcher.updateObject(17, Byte.valueOf((byte)1));
    }

    protected boolean interact(EntityPlayer player)
    {
        ItemStack itemstack = player.inventory.getCurrentItem();

        if (itemstack != null && itemstack.getItem() == Items.flint_and_steel)
        {
            this.worldObj.playSoundEffect(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, "fire.ignite", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
            player.swingItem();

            if (!this.worldObj.isRemote)
            {
                this.ignite();
                itemstack.damageItem(1, player);
                return true;
            }
        }

        return super.interact(player);
    }

    private void explode()
    {
        if (!this.worldObj.isRemote)
        {
            boolean flag = this.worldObj.getGameRules().getBoolean("mobGriefing");
            float f = this.getPowered() ? 2.0F : 1.0F;
            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)this.explosionRadius * f, flag);
            this.setDead();
        }
    }

    public boolean hasIgnited()
    {
        return this.dataWatcher.getWatchableObjectByte(18) != 0;
    }

    public void ignite()
    {
        this.dataWatcher.updateObject(18, Byte.valueOf((byte)1));
    }

    public boolean isAIEnabled()
    {
        return this.field_175494_bm < 1 && this.worldObj.getGameRules().getBoolean("doMobLoot");
    }

    public void func_175493_co()
    {
        ++this.field_175494_bm;
    }
}
