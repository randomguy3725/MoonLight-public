package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TimerManipulationEvent;
import wtf.moonlight.events.impl.player.MoveEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.player.RotationUtils;
import wtf.moonlight.utils.player.SimulatedPlayer;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ModuleInfo(name = "TickBase", category = ModuleCategory.Combat)
public class TickBase extends Module {
    public final SliderValue maxBalance = new SliderValue("Max Balance", 100, 0, 5000, 50, this);
    public final SliderValue delay = new SliderValue("Delay", 50, 0, 1000,50, this);
    public final BoolValue autoSettings = new BoolValue("Auto",true,this);
    public final SliderValue minActiveRange = new SliderValue("Min Active Range", 3f, 0.1f, 7f, 0.1f, this,() -> !autoSettings.get());
    public final SliderValue maxActiveRange = new SliderValue("Max Active Range", 7f, 0.1f, 7f, 0.1f, this,() -> !autoSettings.get());
    public final SliderValue predictTicks = new SliderValue("Predict Ticks", 4, 1, 20, this,() -> !autoSettings.get());
    public final BoolValue displayPredictPos = new BoolValue("Dislay Predict Pos",false,this);
    public final BoolValue check = new BoolValue("Check",false,this);
    private long shifted, previousTime;
    private final TimerUtils timeHelper = new TimerUtils();
    public final List<PredictProcess> predictProcesses = new ArrayList<>();

    @EventTarget
    public void onTimerManipulation(TimerManipulationEvent event) {

        if (mc.thePlayer != null) {
            boolean shouldCharge = false;

            boolean shouldDischarge = shifted >= maxBalance.get();
            
            int value = (int) (autoSettings.get() ? maxBalance.get() / 20 : predictTicks.get()) - 1;

            EntityOtherPlayerMP target = (EntityOtherPlayerMP) PlayerUtils.getTarget(autoSettings.get() ? predictProcesses.get(value).position.distanceTo(mc.thePlayer.getPositionVector()) : maxActiveRange.get() * 3);

            if (target != null &&
                    predictProcesses.get(value).position.distanceTo(target.getPositionVector()) < mc.thePlayer.getPositionVector().distanceTo(target.getPositionVector()) &&
                    (!autoSettings.get() &&
                            MathUtils.inBetween(minActiveRange.get(), maxActiveRange.get(), predictProcesses.get(value).position.distanceTo(target.getPositionVector())) ||
                            autoSettings.get() &&
                                    MathUtils.inBetween(3, mc.thePlayer.getPositionVector().distanceTo(target.getPositionVector()), predictProcesses.get(value).position.distanceTo(target.getPositionVector())))
                    &&

                    mc.thePlayer.canEntityBeSeen(target) && target.canEntityBeSeen(mc.thePlayer) &&
                    (RotationUtils.getRotationDifference(mc.thePlayer, target) <= 90 && check.get() || !check.get()) && !checks() && !predictProcesses.get(value).isCollidedHorizontally) {
                shouldCharge = shifted < maxBalance.get();
            }

            if (shouldCharge && timeHelper.hasTimeElapsed(delay.get())) {
                shifted += event.getTime() - previousTime;
            }

            if (shouldDischarge) {
                shifted = 0;
                timeHelper.reset();
            }

        } else {
            shifted = 0L;
        }

        previousTime = event.getTime();
        event.setTime(event.getTime() - shifted);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {

        predictProcesses.clear();
        if(MovementUtils.isMoving()) {

            SimulatedPlayer simulatedPlayer = SimulatedPlayer.fromClientPlayer(mc.thePlayer.movementInput);

            for (int i = 0; i < (int) (autoSettings.get() ? maxBalance.get() / 20 : predictTicks.get()); i++) {
                simulatedPlayer.tick();
                predictProcesses.add(new PredictProcess(
                        simulatedPlayer.getPos(),
                        simulatedPlayer.fallDistance,
                        simulatedPlayer.motionX,
                        simulatedPlayer.motionY,
                        simulatedPlayer.motionZ,
                        simulatedPlayer.onGround,
                        simulatedPlayer.isCollidedHorizontally
                ));
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if(displayPredictPos.get()) {
            double x = predictProcesses.get((int) (autoSettings.get() ? maxBalance.get() / 20 : predictTicks.get()) - 1).position.xCoord - mc.getRenderManager().viewerPosX;
            double y = predictProcesses.get((int) (autoSettings.get() ? maxBalance.get() / 20 : predictTicks.get()) - 1).position.yCoord - mc.getRenderManager().viewerPosY;
            double z = predictProcesses.get((int) (autoSettings.get() ? maxBalance.get() / 20 : predictTicks.get()) - 1).position.zCoord - mc.getRenderManager().viewerPosZ;
            AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(0.1D, 0.1, 0.1);
            AxisAlignedBB axis = new AxisAlignedBB(box.minX - mc.thePlayer.posX + x, box.minY - mc.thePlayer.posY + y, box.minZ - mc.thePlayer.posZ + z, box.maxX - mc.thePlayer.posX + x, box.maxY - mc.thePlayer.posY + y, box.maxZ - mc.thePlayer.posZ + z);
            RenderUtils.drawAxisAlignedBB(axis,false, true, new Color(50, 255, 255, 150).getRGB());
        }
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

    public static class PredictProcess {
        private final Vec3 position;
        private final float fallDistance;
        private final double motionX;
        private final double motionY;
        private final double motionZ;
        private final boolean onGround;
        private final boolean isCollidedHorizontally;

        public PredictProcess(Vec3 position, float fallDistance, double motionX, double motionY, double motionZ, boolean onGround, boolean isCollidedHorizontally) {
            this.position = position;
            this.fallDistance = fallDistance;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.onGround = onGround;
            this.isCollidedHorizontally = isCollidedHorizontally;
        }
    }

    private boolean checks() {
        return Stream.<Supplier<Boolean>>of(mc.thePlayer::isInLava, mc.thePlayer::isBurning, mc.thePlayer::isInWater,
                () -> mc.thePlayer.isInWeb).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
    }
}