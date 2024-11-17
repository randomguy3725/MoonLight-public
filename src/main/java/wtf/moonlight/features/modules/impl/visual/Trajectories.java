package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

@ModuleInfo(name = "Trajectories", category = ModuleCategory.Visual)
public class Trajectories extends Module {

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        boolean bow;
        boolean potion;
        if (mc.thePlayer.getHeldItem() == null) {
            return;
        }
        if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSnowball) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEgg) && (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion) || !ItemPotion.isSplash(mc.thePlayer.getHeldItem().getItemDamage()))) {
            return;
        }
        bow = (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow);
        potion = (mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion);
        final float throwingYaw = mc.thePlayer.rotationYaw;
        final float throwingPitch = mc.thePlayer.rotationPitch;
        double posX = mc.getRenderManager().renderPosX - MathHelper.cos(throwingYaw / 180.0f * 3.141593f) * 0.16f;
        double posY = mc.getRenderManager().renderPosY + mc.thePlayer.getEyeHeight() - 0.1000000014901161;
        double posZ = mc.getRenderManager().renderPosZ - MathHelper.sin(throwingYaw / 180.0f * 3.141593f) * 0.16f;
        double motionX = -MathHelper.sin(throwingYaw / 180.0f * 3.141593f) * MathHelper.cos(throwingPitch / 180.0f * 3.141593f) * (bow ? 1.0 : 0.4);
        double motionY = -MathHelper.sin((throwingPitch - (potion ? 20 : 0)) / 180.0f * 3.141593f) * (bow ? 1.0 : 0.4);
        double motionZ = MathHelper.cos(throwingYaw / 180.0f * 3.141593f) * MathHelper.cos(throwingPitch / 180.0f * 3.141593f) * (bow ? 1.0 : 0.4);
        final int var6 = 72000 - mc.thePlayer.getItemInUseCount();
        float power = var6 / 20.0f;
        power = (power * power + power * 2.0f) / 3.0f;
        if (power < 0.1) {
            return;
        }
        if (power > 1.0f) {
            power = 1.0f;
        }
        final float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;
        motionX *= (bow ? (power * 2.0f) : 1.0f) * (potion ? 0.5 : 1.5);
        motionY *= (bow ? (power * 2.0f) : 1.0f) * (potion ? 0.5 : 1.5);
        motionZ *= (bow ? (power * 2.0f) : 1.0f) * (potion ? 0.5 : 1.5);
        GlStateManager.resetColor();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GlStateManager.disableTexture2D();
        GL11.glColor4d(10.0, 120.0, 200.0, 200.0);
        GL11.glLineWidth(1.5f);
        GL11.glBegin(3);
        boolean hasLanded = false;
        MovingObjectPosition landingPosition = null;
        while (!hasLanded && posY > 0.0) {
            final Vec3 present = new Vec3(posX, posY, posZ);
            final Vec3 future = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            final MovingObjectPosition possibleLandingStrip = mc.theWorld.rayTraceBlocks(present, future, false, true, false);
            if (possibleLandingStrip != null) {
                if (possibleLandingStrip.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
                    landingPosition = possibleLandingStrip;
                    hasLanded = true;
                }
            } else {
                final Entity entityHit = this.getEntityHit(present, future);
                if (entityHit != null) {
                    landingPosition = new MovingObjectPosition(entityHit);
                    hasLanded = true;
                }
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            final float motionAdjustment = 0.99f;
            motionX *= motionAdjustment;
            motionY *= motionAdjustment;
            motionZ *= motionAdjustment;
            motionY -= (potion ? 0.05 : (bow ? 0.05 : 0.03));
            final double n = posX;
            final double n2 = n - mc.getRenderManager().renderPosX;
            final double n3 = posY;
            final double n4 = n3 - mc.getRenderManager().renderPosY;
            final double n5 = posZ;
            GL11.glVertex3d(n2, n4, n5 - mc.getRenderManager().renderPosZ);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GlStateManager.resetColor();
        if (landingPosition != null && landingPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            RenderUtils.renderBlock(landingPosition.getBlockPos(), new Color(255, 255, 255, 64).getRGB(), true, true);
        }
        if (landingPosition != null && landingPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            final double n11 = -mc.getRenderManager().renderPosX;
            final double n12 = -mc.getRenderManager().renderPosY;
            GL11.glTranslated(n11, n12, -mc.getRenderManager().renderPosZ);
            RenderUtils.drawAxisAlignedBB(landingPosition.entityHit.getEntityBoundingBox().expand(0.225, 0.125, 0.225).offset(0.0, 0.1, 0.0), true, new Color(235, 40, 40, 75).getRGB());
            final double renderPosX = mc.getRenderManager().renderPosX;
            final double renderPosY = mc.getRenderManager().renderPosY;
            GL11.glTranslated(renderPosX, renderPosY, mc.getRenderManager().renderPosZ);
        }
    }

    private ArrayList getEntities() {
        final ArrayList ret = new ArrayList();
        for (final Object e : mc.theWorld.loadedEntityList) {
            if (e != mc.thePlayer && e instanceof EntityLivingBase) {
                ret.add(e);
            }
        }
        return ret;
    }

    private Entity getEntityHit(final Vec3 vecOrig, final Vec3 vecNew) {
        for (final Object o : this.getEntities()) {
            final EntityLivingBase entity = (EntityLivingBase) o;
            if (entity != mc.thePlayer) {
                final float expander = 0.2f;
                final AxisAlignedBB bounding2 = entity.getEntityBoundingBox().expand(expander, expander, expander);
                final MovingObjectPosition possibleEntityLanding = bounding2.calculateIntercept(vecOrig, vecNew);
                if (possibleEntityLanding != null) {
                    return entity;
                }
            }
        }
        return null;
    }
}
