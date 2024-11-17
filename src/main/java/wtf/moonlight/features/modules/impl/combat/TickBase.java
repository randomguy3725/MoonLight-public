package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TimerManipulationEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "TickBase", category = ModuleCategory.Combat)
public class TickBase extends Module {
    public final SliderValue maxBalance = new SliderValue("Max Balance", 100L, 0L, 5000L, this);
    public final SliderValue delay = new SliderValue("Delay", 300L, 0L, 1000L, this);
    public final SliderValue range = new SliderValue("Range", 3f, 0.1f, 7f, 0.1f, this);
    private long shifted, previousTime;
    private final TimerUtils timeHelper = new TimerUtils();
    @EventTarget
    public void onTimerManipulation(TimerManipulationEvent event) {

        boolean shouldCharge = false;

        boolean shouldDischarge = shifted >= maxBalance.get();

        EntityOtherPlayerMP target = (EntityOtherPlayerMP) PlayerUtils.getTarget(range.get() * 2);

        if (target != null) {

            final float width = target.width / 2.0f;
                final float height = target.height;
                final double posXNow = target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks;
                final double posYNow = target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks;
                final double posZNow = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks;
                final double posX2 = target.otherPlayerMPX;
                final double posY2 = target.otherPlayerMPY;
                final double posZ2 = target.otherPlayerMPZ;
                final AxisAlignedBB possibleBoundingBox = new AxisAlignedBB(posX2 - width, posY2, posZ2 - width, posX2 + width, posY2 + height, posZ2 + width);
                final Vec3 positionEyes = mc.thePlayer.getPositionEyes(3.0f);
                final double bestX = MathHelper.clamp_double(positionEyes.xCoord, possibleBoundingBox.minX, possibleBoundingBox.maxX);
                final double bestY = MathHelper.clamp_double(positionEyes.yCoord, possibleBoundingBox.minY, possibleBoundingBox.maxY);
                final double bestZ = MathHelper.clamp_double(positionEyes.zCoord, possibleBoundingBox.minZ, possibleBoundingBox.maxZ);
                final AxisAlignedBB boundingBoxNow = new AxisAlignedBB(posXNow - width, posYNow, posZNow - width, posXNow + width, posYNow + height, posZNow + width);
                final double currentX = MathHelper.clamp_double(positionEyes.xCoord, boundingBoxNow.minX, boundingBoxNow.maxX);
                final double currentY = MathHelper.clamp_double(positionEyes.yCoord, boundingBoxNow.minY, boundingBoxNow.maxY);
                final double currentZ = MathHelper.clamp_double(positionEyes.zCoord, boundingBoxNow.minZ, boundingBoxNow.maxZ);
                final Vec3 currentPosEyes = mc.thePlayer.getPositionEyes(1.0f);
                final Vec3 targetEyes = target.getPositionEyes(1.0f);
                final Vec3 myPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                final double diffX = mc.thePlayer.prevPosX - mc.thePlayer.posX;
                final double diffZ = mc.thePlayer.prevPosZ - mc.thePlayer.posZ;
                final Vec3 myPosBest = myPos.addVector(-diffX * 2.0, 0.0, -diffZ * 2.0);
                final Vec3 myPosBestLast = myPos.addVector(-diffX, 0.0, -diffZ);
                final double myPosForTargetX = myPosBestLast.xCoord + (myPosBest.xCoord - myPosBestLast.xCoord) / 3.0;
                final double myPosForTargetY = myPosBestLast.yCoord + (myPosBest.yCoord - myPosBestLast.yCoord) / 3.0;
                final double myPosForTargetZ = myPosBestLast.zCoord + (myPosBest.zCoord - myPosBestLast.zCoord) / 3.0;
                final float myWidth = target.width / 2.0f;
                final AxisAlignedBB myBB = new AxisAlignedBB(myPosForTargetX - myWidth, myPosForTargetY, myPosForTargetZ - myWidth, myPosForTargetX + myWidth, myPosForTargetY + height, myPosForTargetZ + myWidth);
                final double myBestX = MathHelper.clamp_double(targetEyes.xCoord, myBB.minX, myBB.maxX);
                final double myBestY = MathHelper.clamp_double(targetEyes.yCoord, myBB.minY, myBB.maxY);
                final double myBestZ = MathHelper.clamp_double(targetEyes.zCoord, myBB.minZ, myBB.maxZ);
                if (positionEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) <= range.get() &&
                        targetEyes.distanceTo(new Vec3(myBestX, myBestY, myBestZ)) > range.get() &&
                        currentPosEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) > range.get()) {

                shouldCharge = shifted < maxBalance.get();
            }
        }

        if (shouldCharge && timeHelper.hasTimeElapsed(delay.get())) {
            shifted += event.getTime() - previousTime;
        }

        if (shouldDischarge) {
            shifted = 0;
            timeHelper.reset();
        }

        previousTime = event.getTime();
        event.setTime(event.getTime() - shifted);

    }

    @Override
    public void onDisable() {
        shifted = 0;
    }

    @Override
    public void onEnable() {
        shifted = 0;
        previousTime = (System.nanoTime() / 1000000L) / 1000L;
    }
}