package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearest extends EntityAIBase
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final EntityLiving mob;
    private final Predicate<EntityLivingBase> field_179443_c;
    private final EntityAINearestAttackableTarget.Sorter field_179440_d;
    private EntityLivingBase target;
    private final Class <? extends EntityLivingBase > field_179439_f;

    public EntityAIFindEntityNearest(EntityLiving mobIn, Class <? extends EntityLivingBase > p_i45884_2_)
    {
        this.mob = mobIn;
        this.field_179439_f = p_i45884_2_;

        if (mobIn instanceof EntityCreature)
        {
            LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
        }

        this.field_179443_c = new Predicate<EntityLivingBase>()
        {
            public boolean apply(EntityLivingBase p_apply_1_)
            {
                double d0 = EntityAIFindEntityNearest.this.getFollowRange();

                if (p_apply_1_.isSneaking())
                {
                    d0 *= 0.800000011920929D;
                }

                return !p_apply_1_.isInvisible() && (!((double) p_apply_1_.getDistanceToEntity(EntityAIFindEntityNearest.this.mob) > d0) && EntityAITarget.isSuitableTarget(EntityAIFindEntityNearest.this.mob, p_apply_1_, false, true));
            }
        };
        this.field_179440_d = new EntityAINearestAttackableTarget.Sorter(mobIn);
    }

    public boolean shouldExecute()
    {
        double d0 = this.getFollowRange();
        List<EntityLivingBase> list = this.mob.worldObj.getEntitiesWithinAABB(this.field_179439_f, this.mob.getEntityBoundingBox().expand(d0, 4.0D, d0), this.field_179443_c);
        Collections.sort(list, this.field_179440_d);

        if (list.isEmpty())
        {
            return false;
        }
        else
        {
            this.target = list.get(0);
            return true;
        }
    }

    public boolean continueExecuting()
    {
        EntityLivingBase entitylivingbase = this.mob.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else
        {
            double d0 = this.getFollowRange();
            return !(this.mob.getDistanceSqToEntity(entitylivingbase) > d0 * d0) && (!(entitylivingbase instanceof EntityPlayerMP) || !((EntityPlayerMP) entitylivingbase).theItemInWorldManager.isCreative());
        }
    }

    public void startExecuting()
    {
        this.mob.setAttackTarget(this.target);
        super.startExecuting();
    }

    public void resetTask()
    {
        this.mob.setAttackTarget(null);
        super.startExecuting();
    }

    protected double getFollowRange()
    {
        IAttributeInstance iattributeinstance = this.mob.getEntityAttribute(SharedMonsterAttributes.followRange);
        return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
    }
}
