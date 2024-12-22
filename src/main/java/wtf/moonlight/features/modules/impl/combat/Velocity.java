package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.*;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.movement.LongJump;
import wtf.moonlight.features.modules.impl.world.Scaffold;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.packet.PacketUtils;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.PlayerUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ModuleInfo(name = "Velocity", category = ModuleCategory.Combat)
public class Velocity extends Module {
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Cancel", "Air","Horizontal","Watchdog", "Boost", "Jump Reset", "GrimAC","Intave Reduce","Legit"}, "Air", this);
    private final ModeValue grimMode = new ModeValue("Grim Mode", new String[]{"Reduce", "1.17"}, "Reduce", this, () -> mode.is("GrimAC"));
    private final SliderValue reverseTick = new SliderValue("Boost Tick", 1, 1, 5, 1, this, () -> mode.is("Boost"));
    private final SliderValue reverseStrength = new SliderValue("Boost Strength", 1, 0.1f, 1, 0.01f, this, () -> mode.is("Boost"));
    private final ModeValue jumpResetMode = new ModeValue("Jump Reset Mode", new String[]{"Hurt Time", "Packet"}, "Packet", this, () -> mode.is("Jump Reset"));
    private final SliderValue jumpResetHurtTime = new SliderValue("Jump Reset Hurt Time", 9, 1, 10, 1, this, () -> mode.is("Jump Reset") && jumpResetMode.is("Hurt Time"));
    private final SliderValue intaveHurtTime = new SliderValue("Intave Hurt Time", 9, 1, 10, 1, this, () -> mode.is("Intave Reduce"));
    private final SliderValue intaveFactor = new SliderValue("Intave Factor", 0.6f, 0, 1, 0.05f, this, () -> mode.is("Intave Reduce"));
    private int lastSprint = -1;
    private boolean veloPacket = false;
    private boolean canSpoof, canCancel;
    private int idk = 0;
    private int intaveTick,intaveDamageTick;
    private long lastAttackTime;
    private boolean absorbedVelocity;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());

        switch (mode.get()) {
            case "GrimAC":
                if (grimMode.is("1.17")) {
                    if (canSpoof) {
                        sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                        sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.thePlayer).down(), EnumFacing.DOWN));
                        canSpoof = false;
                    }
                }
                break;

            case "Intave Reduce":
                if (!veloPacket) return;
                intaveTick++;

                if (mc.thePlayer.hurtTime == 2) {
                    intaveDamageTick++;
                    if (mc.thePlayer.onGround && intaveTick % 2 == 0 && intaveDamageTick <= 10) {
                        mc.thePlayer.jump();
                        intaveTick = 0;
                    }
                    veloPacket = false;
                }
                break;

            case "Watchdog":
                if (mc.thePlayer.onGround) {
                    absorbedVelocity = false;
                }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S12PacketEntityVelocity s12 && s12.getEntityID() == mc.thePlayer.getEntityId()) {
            switch (mode.get()) {
                case "Cancel":
                    event.setCancelled(true);
                    break;

                case "Air":
                    if (!isEnabled(LongJump.class)) {
                        event.setCancelled(true);
                        if (mc.thePlayer.onGround)
                            mc.thePlayer.motionY = (double) s12.getMotionY() / 8000;
                    }
                    break;
                case "Horizontal":
                    if (!isEnabled(LongJump.class)) {
                        s12.motionX = (int) (mc.thePlayer.motionX * 8000);
                        s12.motionZ = (int) (mc.thePlayer.motionZ * 8000);
                    }
                    break;
                case "Boost":
                    if (getModule(KillAura.class).target != null)
                        veloPacket = true;
                    if (getModule(KillAura.class).target == null) {
                        event.setCancelled(true);
                        mc.thePlayer.motionY = (double) s12.getMotionY() / 8000;
                    }
                    break;

                case "Watchdog":
                    if (!mc.thePlayer.onGround) {
                        if (!absorbedVelocity) {
                            event.setCancelled(true);
                            absorbedVelocity = true;
                            return;
                        }
                    }
                    s12.motionX = (int) (mc.thePlayer.motionX * 8000);
                    s12.motionZ = (int) (mc.thePlayer.motionZ * 8000);
                    break;

                case "GrimAC":
                    switch (grimMode.get()) {
                        case "Reduce":
                            if (getModule(KillAura.class).target != null && !isEnabled(Scaffold.class)) {

                                event.setCancelled(true);

                                if (!mc.thePlayer.serverSprintState) {
                                    PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                                }

                                for (int i = 0; i < 8; i++) {
                                    sendPacketNoEvent(new C02PacketUseEntity(getModule(KillAura.class).target, C02PacketUseEntity.Action.ATTACK));
                                    sendPacketNoEvent(new C0APacketAnimation());
                                }

                                double velocityX = s12.getMotionX() / 8000.0;
                                double velocityZ = s12.getMotionZ() / 8000.0;

                                if (MathHelper.sqrt_double(velocityX * velocityX * velocityZ * velocityZ) <= 3F) {
                                    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                                } else {
                                    mc.thePlayer.motionX = velocityX * 0.078;
                                    mc.thePlayer.motionZ = velocityZ * 0.078;
                                }

                                mc.thePlayer.motionY = s12.getMotionY() / 8000.0;

                            }

                            break;
                        case "1.17":
                            if (canCancel) {
                                canCancel = false;
                                canSpoof = true;
                                event.setCancelled(true);
                            }
                            break;
                    }
                    break;

                case "Jump Reset":
                    if (jumpResetMode.is("Packet") && s12.getMotionY() > 0) {
                        veloPacket = true;
                    }
                    break;

                case "Intave Reduce":
                    veloPacket = true;
                    break;
            }
        }

        if (mode.is("GrimAC") && grimMode.is("1.17")) {
            if (event.getPacket() instanceof S19PacketEntityStatus s19PacketEntityStatus) {

                if (s19PacketEntityStatus.getEntity(mc.theWorld) == mc.thePlayer) {
                    canCancel = true;
                }
            }
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setTag(mode.get());
        switch (mode.get()) {
            case "GrimAC": {
                if (grimMode.get().equals("Reduce")) {
                    if (event.isPre()) {
                        if (lastSprint == 0) {
                            lastSprint--;
                            if (!MovementUtils.canSprint(true))
                                sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                        } else if (lastSprint > 0) {
                            lastSprint--;
                            if (mc.thePlayer.onGround && !MovementUtils.canSprint(true)) {
                                lastSprint = -1;
                                sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                            }
                        }
                    }
                }
            }
            case "Boost":
                if (veloPacket) {
                    idk++;
                }
                if (idk == reverseTick.get()) {
                    MovementUtils.strafe(MovementUtils.getSpeed() * reverseStrength.get());
                    veloPacket = false;
                    idk = 0;
                }
                break;
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if (mode.is("Jump Reset") && (jumpResetMode.is("Packet") && veloPacket || jumpResetMode.is("Hurt Time") && mc.thePlayer.hurtTime >= jumpResetHurtTime.get())) {
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !checks()) {
                mc.thePlayer.jump();
                veloPacket = false;
            }
        }
    }

    @EventTarget
    public void onAttack(AttackEvent event){
        if(mode.is("Intave Reduce")){
            if (mc.thePlayer.hurtTime == intaveHurtTime.get() && System.currentTimeMillis() - lastAttackTime <= 8000) {
                mc.thePlayer.motionX *= intaveFactor.get();
                mc.thePlayer.motionZ *= intaveFactor.get();
            }

            lastAttackTime = System.currentTimeMillis();
        }
    }

    @EventTarget
    public void onMoveInput(MoveInputEvent event) {
        if (mode.is("Legit") && getModule(KillAura.class).target != null && mc.thePlayer.hurtTime > 0) {
            ArrayList<Vec3> vec3s = new ArrayList<>();
            HashMap<Vec3, Integer> map = new HashMap<>();
            Vec3 playerPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            Vec3 onlyForward = PlayerUtils.getPredictedPos(1.0F, 0.0F).add(playerPos);
            Vec3 strafeLeft = PlayerUtils.getPredictedPos(1.0F, 1.0F).add(playerPos);
            Vec3 strafeRight = PlayerUtils.getPredictedPos(1.0F, -1.0F).add(playerPos);
            map.put(onlyForward, 0);
            map.put(strafeLeft, 1);
            map.put(strafeRight, -1);
            vec3s.add(onlyForward);
            vec3s.add(strafeLeft);
            vec3s.add(strafeRight);
            Vec3 targetVec = new Vec3(getModule(KillAura.class).target.posX, getModule(KillAura.class).target.posY, getModule(KillAura.class).target.posZ);
            vec3s.sort(Comparator.comparingDouble(targetVec::distanceXZTo));
            if (!mc.thePlayer.movementInput.sneak) {
                System.out.println(map.get(vec3s.get(0)));
                mc.thePlayer.movementInput.moveStrafe = map.get(vec3s.get(0));
            }
        }
    }

    private boolean checks() {
        return Stream.<Supplier<Boolean>>of(mc.thePlayer::isInLava, mc.thePlayer::isBurning, mc.thePlayer::isInWater,
                () -> mc.thePlayer.isInWeb).map(Supplier::get).anyMatch(Boolean.TRUE::equals);
    }
}

