/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight-public
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MathHelper;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.MoveInputEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.movement.Scaffold;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.player.RotationUtils;

@ModuleInfo(name = "KeepRange", category = ModuleCategory.Legit)
public class KeepRange extends Module {
    private final SliderValue range = new SliderValue("Range", 3, 0, 6, 0.1f, this);
    private final BoolValue disableNearEdge = new BoolValue("Disable Near Edge", true, this);
    private final SliderValue edgeRange = new SliderValue("Edge Range", 5, 0, 6, this, () -> !disableNearEdge.get());
    private final ModeValue mode = new ModeValue("Mode", new String[]{"BackWards", "Stop"}, "Stop", this);
    private final SliderValue combo = new SliderValue("Combo To Start", 2, 0, 6, this);
    private int ticksSinceAttack;
    private boolean edge;
    private int row;

    @EventTarget
    public void onTick(TickEvent event) {
        if (!mc.thePlayer.onGround) return;

        edge = false;
        int range = (int) edgeRange.get();

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -5; y <= 0; y++) {
                    boolean air = PlayerUtils.blockRelativeToPlayer(x, y, z) instanceof BlockAir;

                    if (!air) {
                        break;
                    }

                    if (y == 0) {
                        edge = true;
                        return;
                    }
                }
            }
        }
    }

    @EventTarget
    public void onMoveInput(MoveInputEvent event) {
        EntityLivingBase target;

        if (getModule(KillAura.class).target != null && !isEnabled(Scaffold.class)) {
            target = getModule(KillAura.class).target;
        } else {
            return;
        }

        double range = this.range.get();

        if (ticksSinceAttack <= 7) range -= 0.2;

        if (target == null || (edge && disableNearEdge.get())) {
            row = 0;
            return;
        }

        if (target.hurtTime > 0) row += 1;
        if (mc.thePlayer.hurtTime > 0) row = 0;

        if (row <= combo.get() * 8 && combo.get() > 0) {
            return;
        }

        if (PlayerUtils.getDistanceToEntityBox(target) < range - 0.05) {
            final float forward = event.getForward();
            final float strafe = event.getStrafe();

            final double angle = MathHelper.wrapAngleTo180_double(RotationUtils.currentRotation[0] - 180);
            if (forward == 0 && strafe == 0) {
                return;
            }

            float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

            for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
                for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                    if (predictedStrafe == 0 && predictedForward == 0) continue;

                    final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MovementUtils.getDirection(predictedForward, predictedStrafe, mc.thePlayer.rotationYaw)));
                    final double difference = MathUtils.wrappedDifference(angle, predictedAngle);

                    if (difference < closestDifference) {
                        closestDifference = (float) difference;
                        closestForward = predictedForward;
                        closestStrafe = predictedStrafe;
                    }
                }
            }

            switch (mode.get()) {
                case "Stop":
                    if (closestForward == forward * -1) event.setForward(0);
                    if (closestStrafe == strafe * -1) event.setStrafe(0);
                    break;

                case "BackWards":
                    event.setForward(closestForward);
                    event.setStrafe(closestStrafe);
                    break;
            }
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            ticksSinceAttack = 0;
        }
        ticksSinceAttack++;
    }
}
