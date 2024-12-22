package wtf.moonlight.utils.player;

import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import wtf.moonlight.events.annotations.EventPriority;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.*;
import wtf.moonlight.features.modules.impl.visual.Rotation;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.math.MathUtils;

import java.util.List;
import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;

public class RotationUtils implements InstanceAccess {
    public static float[] currentRotation = null, serverRotation = new float[]{}, previousRotation = null;
    public static MovementCorrection currentCorrection = MovementCorrection.OFF;
    public static RotationSmoother rotationSmoother = RotationSmoother.OFF;
    private static float maxHAcceleration,maxVAcceleration, accelerationError, constantError;
    private static float hSpeed, vSpeed;
    private static boolean enabled;
    private static boolean smoothlyReset;
    public static boolean fixSprint = false;

    public static boolean shouldRotate() {
        return currentRotation != null;
    }

    public static void setRotation(float[] rotation) {
        setRotation(rotation, MovementCorrection.OFF);
    }

    public static void setRotation(float[] rotation, final MovementCorrection correction) {
        rotationSmoother = RotationSmoother.OFF;
        RotationUtils.previousRotation = smooth(serverRotation, rotation);
        RotationUtils.currentRotation = smooth(serverRotation, rotation);
        currentCorrection = correction;
        smoothlyReset = false;
        enabled = true;
    }

    public static void setRotation(float[] rotation, final MovementCorrection correction, float hSpeed, float vSpeed, boolean smoothlyReset) {
        rotationSmoother = RotationSmoother.LINEAR;
        RotationUtils.hSpeed = hSpeed;
        RotationUtils.vSpeed = vSpeed;
        RotationUtils.previousRotation = smooth(serverRotation, rotation);
        RotationUtils.currentRotation = smooth(serverRotation, rotation);
        currentCorrection = correction;
        RotationUtils.smoothlyReset = smoothlyReset;
        enabled = true;
    }

    public static void setRotation(float[] rotation, final MovementCorrection correction, float maxHAcceleration, float maxVAcceleration, float accelerationError, float constantError, boolean smoothlyReset) {
        rotationSmoother = RotationSmoother.ACCELERATION;
        RotationUtils.maxHAcceleration = maxHAcceleration;
        RotationUtils.maxVAcceleration = maxVAcceleration;
        RotationUtils.accelerationError = accelerationError;
        RotationUtils.constantError = constantError;
        RotationUtils.previousRotation = smooth(serverRotation, rotation);
        RotationUtils.currentRotation = smooth(serverRotation, rotation);
        currentCorrection = correction;
        RotationUtils.smoothlyReset = smoothlyReset;
        enabled = true;
    }


    @EventTarget
    @EventPriority(1000)
    public void onUpdate(UpdateEvent event) {
        if (shouldRotate() && (fixSprint || currentCorrection != MovementCorrection.OFF)) {
            if (Math.abs(currentRotation[0] % 360 - Math.toDegrees(MovementUtils.getDirection()) % 360) > 45) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
                mc.thePlayer.setSprinting(false);
            }
        }
    }

    @EventTarget
    @EventPriority(-100)
    public void onRotationUpdate(UpdateEvent event) {
        double distanceToPlayerRotation = getRotationDifference(Objects.requireNonNullElse(currentRotation, serverRotation), new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch});

        if (!enabled) {

            if (!smoothlyReset || distanceToPlayerRotation < 1) {
                resetRotation();
                return;
            }

            if (smoothlyReset && distanceToPlayerRotation > 0) {
                accelerationError = 0;
                constantError = 0;
                RotationUtils.currentRotation = (smooth(Objects.requireNonNullElse(currentRotation, serverRotation), new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch}));
            }
        }
        enabled = false;
    }

    @EventTarget
    private void onStrafe(StrafeEvent event) {
        if (shouldRotate()) {
            if (currentCorrection == MovementCorrection.STRICT) {
                event.setYaw(currentRotation[0]);
            }
            if (currentCorrection == MovementCorrection.SILENT) {
                EntityPlayerSP player = mc.thePlayer;

                double diff = Math.toRadians(player.rotationYaw - currentRotation[0]);

                float calcForward;
                float calcStrafe;

                float strafe = event.getStrafe() / 0.98f;
                float forward = event.getForward() / 0.98f;

                float modifiedForward = (float) Math.ceil(Math.abs(forward)) * Math.signum(forward);
                float modifiedStrafe = (float) Math.ceil(Math.abs(strafe)) * Math.signum(strafe);

                calcForward = Math.round(modifiedForward * MathHelper.cos((float) diff) + modifiedStrafe * MathHelper.sin((float) diff));
                calcStrafe = Math.round(modifiedStrafe * MathHelper.cos((float) diff) - modifiedForward * MathHelper.sin((float) diff));

                float f = (event.getForward() != 0f) ? event.getForward() : event.getStrafe();

                calcForward *= Math.abs(f);
                calcStrafe *= Math.abs(f);

                event.setYaw(currentRotation[0]);
                event.setStrafe(calcStrafe);
                event.setForward(calcForward);

                /*double d = calcStrafe * calcStrafe + calcForward * calcForward;

                if (d >= 1.0E-4f) {
                    d = friction / Math.sqrt(d);

                    calcStrafe *= (float) d;
                    calcForward *= (float) d;

                    double yawRad = Math.toRadians(currentRotation[0]);
                    double yawSin = MathHelper.sin((float) yawRad);
                    double yawCos = MathHelper.cos((float) yawRad);

                    player.motionX += calcStrafe * yawCos - calcForward * yawSin;
                    player.motionZ += calcForward * yawCos + calcStrafe * yawSin;
                }
                event.setCancelled(true);*/
            }
        }
    }

    @EventTarget
    private void onJump(JumpEvent event) {
        if (shouldRotate()) {
            if (currentCorrection != MovementCorrection.OFF) {
                event.setYaw(currentRotation[0]);
            }
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if (!(packet instanceof C03PacketPlayer packetPlayer))
            return;

        if (!packetPlayer.rotating)
            return;

        if (shouldRotate()) {
            packetPlayer.yaw = currentRotation[0];
            packetPlayer.pitch = currentRotation[1];
        }

        serverRotation = new float[]{packetPlayer.yaw, packetPlayer.pitch};

    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        resetRotation();
    }

    @EventTarget
    @EventPriority(-100)
    public void onMotion(MotionEvent event) {
        if (event.isPost() && smoothlyReset || !smoothlyReset) {
            double distanceToPlayerRotation = getRotationDifference(Objects.requireNonNullElse(currentRotation, serverRotation), new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch});

            if (!enabled) {

                if (!smoothlyReset || distanceToPlayerRotation < 1) {
                    resetRotation();
                    return;
                }

                if (distanceToPlayerRotation > 0) {
                    accelerationError = 0;
                    constantError = 0;
                    RotationUtils.currentRotation = (smooth(Objects.requireNonNullElse(currentRotation, serverRotation), new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch}));
                }
            }
            enabled = false;
        }
    }

    @EventTarget
    public void onLook(LookEvent event){
        if(shouldRotate() && INSTANCE.getModuleManager().getModule(Rotation.class).fixAim.get())
            event.rotation = currentRotation;
    }
    private static void resetRotation() {
        enabled = false;
        RotationUtils.currentRotation = null;
        currentCorrection = MovementCorrection.OFF;
    }

    public static float[] smooth(final float[] currentRotation, final float[] targetRotation) {
        switch (rotationSmoother) {
            case OFF -> {
                return applyGCDFix(currentRotation, targetRotation);
            }
            case LINEAR -> {
                return linearSmooth(currentRotation, targetRotation);
            }
            case ACCELERATION -> {
                return accelerationSmooth(currentRotation, targetRotation);
            }
        }
        return null;
    }

    public static float[] accelerationSmooth(final float[] currentRotation, final float[] targetRotation) {
        float[] prevRotation = previousRotation;
        float prevYawDiff = getAngleDifference(currentRotation[0], prevRotation[0]);
        float prevPitchDiff = getAngleDifference(currentRotation[1], prevRotation[1]);
        float yawDiff = getAngleDifference(targetRotation[0], currentRotation[0]);
        float pitchDiff = getAngleDifference(targetRotation[1], currentRotation[1]);

        float[] newDiff = computeTurnSpeed(prevYawDiff, prevPitchDiff, yawDiff, pitchDiff);

        float[] rot = new float[]{
                (currentRotation[0] + newDiff[0]),
                (currentRotation[1] + newDiff[1])
        };

        return applyGCDFix(serverRotation, rot);
    }

    public static float[] linearSmooth(final float[] prevRotation, final float[] currentRotation) {
        float yawDifference = getAngleDifference(currentRotation[0], prevRotation[0]);
        float pitchDifference = getAngleDifference(currentRotation[1], prevRotation[1]);

        double rotationDifference = hypot(abs(yawDifference), abs(pitchDifference));

        float straightLineYaw = (float) (abs(yawDifference / rotationDifference) * hSpeed);
        float straightLinePitch = (float) (abs(pitchDifference / rotationDifference) * vSpeed);

        return applyGCDFix(currentRotation, new float[]{prevRotation[0] + Math.max(-straightLineYaw, Math.min(straightLineYaw, yawDifference)), prevRotation[1] + Math.max(-straightLinePitch, Math.min(straightLinePitch, pitchDifference))});
    }

    public static float[] applyGCDFix(float[] prevRotation, float[] currentRotation) {
        final float f = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 100000) * 0.6F + 0.2F);
        final double gcd = f * f * f * 8.0F * 0.15D;
        final float yaw = prevRotation[0] + (float) (Math.round((currentRotation[0] - prevRotation[0]) / gcd) * gcd);
        final float pitch = prevRotation[1] + (float) (Math.round((currentRotation[1] - prevRotation[1]) / gcd) * gcd);

        return new float[]{yaw, pitch};
    }

    private static float[] computeTurnSpeed(float prevYawDiff, float prevPitchDiff, float yawDiff, float pitchDiff) {

        float yawAccel = getAngleDifference(yawDiff, prevYawDiff);
        yawAccel = Math.max(-maxHAcceleration, Math.min(yawAccel, maxHAcceleration));

        float pitchAccel = getAngleDifference(pitchDiff, prevPitchDiff);
        pitchAccel = Math.max(-maxVAcceleration, Math.min(pitchAccel, maxVAcceleration));

        float yawError = yawAccel * errorMult() + constantError();
        float pitchError = pitchAccel * errorMult() + constantError();

        return new float[]{prevYawDiff + yawAccel + yawError, prevPitchDiff + pitchAccel + pitchError};
    }

    public static float errorMult() {
        return MathUtils.nextFloat(-accelerationError, accelerationError);
    }

    public static float constantError() {
        return MathUtils.nextFloat(-constantError, constantError);
    }

    public static float getAngleDifference(float a, float b) {
        return MathHelper.wrapAngleTo180_float(a - b);
    }

    public static float[] getAngles(Entity entity) {
        if (entity == null) return null;
        final EntityPlayerSP player = mc.thePlayer;

        final double diffX = entity.posX - player.posX,
                diffY = entity.posY + (entity.getEyeHeight() / 5 * 3) - (player.posY + player.getEyeHeight()),
                diffZ = entity.posZ - player.posZ, dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F,
                pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[]{player.rotationYaw + MathHelper.wrapAngleTo180_float(
                yaw - player.rotationYaw), player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - player.rotationPitch)};
    }

    public static float i(final double n, final double n2) {
        return (float) (Math.atan2(n - mc.thePlayer.posX, n2 - mc.thePlayer.posZ) * 57.295780181884766 * -1.0);
    }

    public static double distanceFromYaw(final Entity entity) {
        return Math.abs(MathHelper.wrapAngleTo180_double(i(entity.posX, entity.posZ) - mc.thePlayer.rotationYaw));
    }

    public static float getRotationDifference(final Entity entity) {
        float[] target = RotationUtils.getRotations(entity.posX,entity.posY + entity.getEyeHeight(),entity.posZ);
        return (float) hypot(Math.abs(getAngleDifference(target[0], mc.thePlayer.rotationYaw)), Math.abs(target[1] - mc.thePlayer.rotationPitch));
    }
    public static float getRotationDifference(final Entity entity,final Entity entity2) {
        float[] target = RotationUtils.getRotations(entity.posX,entity.posY + entity.getEyeHeight(),entity.posZ);
        float[] target2 = RotationUtils.getRotations(entity2.posX,entity2.posY + entity2.getEyeHeight(),entity2.posZ);
        return (float) hypot(Math.abs(getAngleDifference(target[0], target2[0])), Math.abs(target[1] - target2[1]));
    }

    public static float getRotationDifference(final float[] a, final float[] b) {
        return (float) hypot(Math.abs(getAngleDifference(a[0], b[0])), Math.abs(a[1] - b[1]));
    }

    public static MovingObjectPosition rayTrace(float[] rot, double blockReachDistance, float partialTicks) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 vec31 = mc.thePlayer.getLookCustom(rot[0], rot[1]);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public static MovingObjectPosition rayTrace(double blockReachDistance, float partialTicks) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 vec31 = mc.thePlayer.getLookCustom(currentRotation[0], currentRotation[1]);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public static float[] getRotations(BlockPos blockPos, EnumFacing enumFacing) {
        return getRotations(blockPos, enumFacing, 0.25, 0.25);
    }

    public static float[] getRotations(BlockPos blockPos, EnumFacing enumFacing, double xz, double y) {
        double d = blockPos.getX() + 0.5 - mc.thePlayer.posX + enumFacing.getFrontOffsetX() * xz;
        double d2 = blockPos.getZ() + 0.5 - mc.thePlayer.posZ + enumFacing.getFrontOffsetZ() * xz;
        double d3 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - blockPos.getY() - enumFacing.getFrontOffsetY() * y;
        double d4 = MathHelper.sqrt_double(d * d + d2 * d2);
        float f = (float) (Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float) (Math.atan2(d3, d4) * 180.0 / Math.PI);
        return new float[]{MathHelper.wrapAngleTo180_float(f), f2};
    }

    public static float[] getRotations(double rotX, double rotY, double rotZ, double startX, double startY, double startZ) {
        double x = rotX - startX;
        double y = rotY - startY;
        double z = rotZ - startZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        return getRotations(posX, posY, posZ, mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static float[] getRotations(Vec3 vec) {
        return getRotations(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static float[] getRotationToBlock(BlockPos blockPos,EnumFacing direction) {

        double centerX = blockPos.getX() + 0.5 + direction.getFrontOffsetX() * 0.5;
        double centerY = blockPos.getY() + 0.5 + direction.getFrontOffsetY() * 0.5;
        double centerZ = blockPos.getZ() + 0.5 + direction.getFrontOffsetZ() * 0.5;

        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double playerZ = mc.thePlayer.posZ;

        double deltaX = centerX - playerX;
        double deltaY = centerY - playerY;
        double deltaZ = centerZ - playerZ;

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F);
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, distanceXZ));

        return new float[]{yaw, pitch};
    }

    public static float clampTo90(final float n) {
        return MathHelper.clamp_float(n, -90, 90);
    }

    public static float calculateYawFromSrcToDst(final float yaw,
                                                 final double srcX,
                                                 final double srcZ,
                                                 final double dstX,
                                                 final double dstZ) {
        final double xDist = dstX - srcX;
        final double zDist = dstZ - srcZ;
        final float var1 = (float) (StrictMath.atan2(zDist, xDist) * 180.0 / Math.PI) - 90.0F;
        return yaw + MathHelper.wrapAngleTo180_float(var1 - yaw);
    }

    public static Vec3 getBestHitVec(final Entity entity) {
        final Vec3 positionEyes = mc.thePlayer.getPositionEyes(1);
        final AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
        final double ex = MathHelper.clamp_double(positionEyes.xCoord, entityBoundingBox.minX, entityBoundingBox.maxX);
        final double ey = MathHelper.clamp_double(positionEyes.yCoord, entityBoundingBox.minY, entityBoundingBox.maxY);
        final double ez = MathHelper.clamp_double(positionEyes.zCoord, entityBoundingBox.minZ, entityBoundingBox.maxZ);
        return new Vec3(ex, ey, ez);
    }

    public static float getYaw(@NotNull BlockPos pos) {
        return getYaw(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    public static float getYaw(@NotNull AbstractClientPlayer from, @NotNull Vec3 pos) {
        return from.rotationYaw +
                MathHelper.wrapAngleTo180_float(
                        (float) Math.toDegrees(Math.atan2(pos.zCoord - from.posZ, pos.xCoord - from.posX)) - 90f - from.rotationYaw
                );
    }

    public static float getYaw(@NotNull Vec3 pos) {
        return getYaw(mc.thePlayer, pos);
    }

    public static float getPitch(@NotNull BlockPos pos) {
        return getPitch(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    public static float getPitch(@NotNull AbstractClientPlayer from, @NotNull Vec3 pos) {
        double diffX = pos.xCoord - from.posX;
        double diffY = pos.yCoord - (from.posY + from.getEyeHeight());
        double diffZ = pos.zCoord - from.posZ;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return from.rotationPitch + MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - from.rotationPitch);
    }

    public static float getPitch(@NotNull Vec3 pos) {
        return getPitch(mc.thePlayer, pos);
    }

    public static MovingObjectPosition rayCast(final float[] rotation, final double range, final float expand) {
        return rayCast(rotation, range, expand, mc.thePlayer);
    }

    public static MovingObjectPosition rayCast(final float[] rotation, final double range) {
        return rayCast(rotation, range, 0);
    }

    public static MovingObjectPosition rayCast(final float[] rotation, final double range, final float expand, Entity entity) {
        final float partialTicks = mc.timer.renderPartialTicks;
        MovingObjectPosition objectMouseOver;

        if (entity != null && mc.theWorld != null) {
            objectMouseOver = entity.rayTraceCustom(range, rotation[0], rotation[1]);
            double d1 = range;
            final Vec3 vec3 = entity.getPositionEyes(partialTicks);

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }

            final Vec3 vec31 = mc.thePlayer.getLookCustom(rotation[0], rotation[1]);
            final Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                final float f1 = entity1.getCollisionBorderSize() + expand;
                final AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }

            return objectMouseOver;
        }

        return null;
    }

    public static float[] faceTrajectory(Entity target, boolean predict, float predictSize,float gravity,float velocity) {
        EntityPlayerSP player = mc.thePlayer;

        double posX = target.posX + (predict ? (target.posX - target.prevPosX) * predictSize : 0.0) - (player.posX + (predict ? player.posX - player.prevPosX : 0.0));
        double posY = target.getEntityBoundingBox().minY + (predict ? (target.getEntityBoundingBox().minY - target.prevPosY) * predictSize : 0.0) + target.getEyeHeight() - 0.15 - (player.getEntityBoundingBox().minY + (predict ? player.posY - player.prevPosY : 0.0)) - player.getEyeHeight();
        double posZ = target.posZ + (predict ? (target.posZ - target.prevPosZ) * predictSize : 0.0) - (player.posZ + (predict ? player.posZ - player.prevPosZ : 0.0));
        double posSqrt = Math.sqrt(posX * posX + posZ * posZ);

        /*velocity = player.getItemInUseDuration() / 20f;*/
        velocity = Math.min((velocity * velocity + velocity * 2) / 3, 1f);

        float gravityModifier = 0.12f * gravity;

        return new float[]{
                (float) Math.toDegrees(Math.atan2(posZ, posX)) - 90f,
                (float) -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(
                        velocity * velocity * velocity * velocity - gravityModifier * (gravityModifier * posSqrt * posSqrt + 2 * posY * velocity * velocity)
                )) / (gravityModifier * posSqrt)))
        };
    }

    public static float[] faceTrajectory(Entity target, boolean predict, float predictSize) {

        float gravity = 0.03f;
        float velocity = 0;

        return faceTrajectory(target,predict,predictSize,gravity,velocity);
    }
    public static Vec3 heuristics(Entity entity, Vec3 xyz) {
        double boxSize = 0.2;
        float f11 = entity.getCollisionBorderSize();
        double minX = MathHelper.clamp_double(
                xyz.xCoord - boxSize, entity.getEntityBoundingBox().minX - (double)f11, entity.getEntityBoundingBox().maxX + (double)f11
        );
        double minY = MathHelper.clamp_double(
                xyz.yCoord - boxSize, entity.getEntityBoundingBox().minY - (double)f11, entity.getEntityBoundingBox().maxY + (double)f11
        );
        double minZ = MathHelper.clamp_double(
                xyz.zCoord - boxSize, entity.getEntityBoundingBox().minZ - (double)f11, entity.getEntityBoundingBox().maxZ + (double)f11
        );
        double maxX = MathHelper.clamp_double(
                xyz.xCoord + boxSize, entity.getEntityBoundingBox().minX - (double)f11, entity.getEntityBoundingBox().maxX + (double)f11
        );
        double maxY = MathHelper.clamp_double(
                xyz.yCoord + boxSize, entity.getEntityBoundingBox().minY - (double)f11, entity.getEntityBoundingBox().maxY + (double)f11
        );
        double maxZ = MathHelper.clamp_double(
                xyz.zCoord + boxSize, entity.getEntityBoundingBox().minZ - (double)f11, entity.getEntityBoundingBox().maxZ + (double)f11
        );
        xyz.xCoord = MathHelper.clamp_double(xyz.xCoord + MathUtils.randomSin(), minX, maxX);
        xyz.yCoord = MathHelper.clamp_double(xyz.yCoord + MathUtils.randomSin(), minY, maxY);
        xyz.zCoord = MathHelper.clamp_double(xyz.zCoord + MathUtils.randomSin(), minZ, maxZ);
        return xyz;
    }
}
