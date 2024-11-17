package net.optifine.entity.model.anim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionFloat;

public enum RenderEntityParameterFloat implements IExpressionFloat
{
    LIMB_SWING("limb_swing"),
    LIMB_SWING_SPEED("limb_speed"),
    AGE("age"),
    HEAD_YAW("head_yaw"),
    HEAD_PITCH("head_pitch"),
    SCALE("scale"),
    HEALTH("health"),
    HURT_TIME("hurt_time"),
    IDLE_TIME("idle_time"),
    MAX_HEALTH("max_health"),
    MOVE_FORWARD("move_forward"),
    MOVE_STRAFING("move_strafing"),
    PARTIAL_TICKS("partial_ticks"),
    POS_X("pos_x"),
    POS_Y("pos_y"),
    POS_Z("pos_z"),
    REVENGE_TIME("revenge_time"),
    SWING_PROGRESS("swing_progress");

    private final String name;
    private final RenderManager renderManager;
    private static final RenderEntityParameterFloat[] VALUES = values();

    RenderEntityParameterFloat(String name)
    {
        this.name = name;
        this.renderManager = Minecraft.getMinecraft().getRenderManager();
    }

    public String getName()
    {
        return this.name;
    }

    public ExpressionType getExpressionType()
    {
        return ExpressionType.FLOAT;
    }

    public float eval()
    {
        Render render = this.renderManager.renderRender;

        if (render == null)
        {
            return 0.0F;
        }
        else
        {
            if (render instanceof RendererLivingEntity rendererlivingentity)
            {

                switch (this)
                {
                    case LIMB_SWING:
                        return rendererlivingentity.renderLimbSwing;

                    case LIMB_SWING_SPEED:
                        return rendererlivingentity.renderLimbSwingAmount;

                    case AGE:
                        return rendererlivingentity.renderAgeInTicks;

                    case HEAD_YAW:
                        return rendererlivingentity.renderHeadYaw;

                    case HEAD_PITCH:
                        return rendererlivingentity.renderHeadPitch;

                    case SCALE:
                        return rendererlivingentity.renderScaleFactor;

                    default:
                        EntityLivingBase entitylivingbase = rendererlivingentity.renderEntity;

                        if (entitylivingbase == null)
                        {
                            return 0.0F;
                        }

                        switch (this)
                        {
                            case HEALTH:
                                return entitylivingbase.getHealth();

                            case HURT_TIME:
                                return (float)entitylivingbase.hurtTime;

                            case IDLE_TIME:
                                return (float)entitylivingbase.getAge();

                            case MAX_HEALTH:
                                return entitylivingbase.getMaxHealth();

                            case MOVE_FORWARD:
                                return entitylivingbase.moveForward;

                            case MOVE_STRAFING:
                                return entitylivingbase.moveStrafing;

                            case POS_X:
                                return (float)entitylivingbase.posX;

                            case POS_Y:
                                return (float)entitylivingbase.posY;

                            case POS_Z:
                                return (float)entitylivingbase.posZ;

                            case REVENGE_TIME:
                                return (float)entitylivingbase.getRevengeTimer();

                            case SWING_PROGRESS:
                                return entitylivingbase.getSwingProgress(rendererlivingentity.renderPartialTicks);
                        }
                }
            }

            return 0.0F;
        }
    }

    public static RenderEntityParameterFloat parse(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            for (int i = 0; i < VALUES.length; ++i)
            {
                RenderEntityParameterFloat renderentityparameterfloat = VALUES[i];

                if (renderentityparameterfloat.getName().equals(str))
                {
                    return renderentityparameterfloat;
                }
            }

            return null;
        }
    }
}
