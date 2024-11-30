package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.MoveEvent;
import wtf.moonlight.events.impl.player.StrafeEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.exploit.Disabler;
import wtf.moonlight.features.modules.impl.world.Scaffold;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.misc.DebugUtils;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.player.RotationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@ModuleInfo(name = "Speed", category = ModuleCategory.Movement, key = Keyboard.KEY_V)
public class Speed extends Module {
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Watchdog", "EntityCollide", "BlocksMC", "Intave", "NCP"}, "Watchdog", this);
    private final ModeValue wdMode = new ModeValue("Watchdog Mode", new String[]{"Basic","Custom", "Glide","Full Strafe"}, "Basic", this, () -> mode.is("Watchdog"));
    private final BoolValue boost = new BoolValue("Boost", true, this, () -> mode.is("Watchdog"));
    private final ModeValue fullStrafeMode = new ModeValue("Full Strafe Mode",new String[]{"A","B"},"A",this,() -> wdMode.is("Full Strafe"));
    private final BoolValue fastFall = new BoolValue("Fast Fall", true, this, () -> mode.is("Watchdog") && !wdMode.is("Glide") && !wdMode.is("Full Strafe"));
    private final ModeValue wdFastFallMode = new ModeValue("Fast Fall Mode", new String[]{"Normal","Test 1","Test 2","Test 3","Test 4","Test 5","Predict", "Predict 2","8 Tick"}, "8 Tick", this, () -> mode.is("Watchdog") && fastFall.canDisplay() && fastFall.get());
    private final BoolValue strafe = new BoolValue("Strafe", false, this, () -> fastFall.canDisplay() && fastFall.get());
    private final SliderValue predictTicks = new SliderValue("Predict Ticks",5,4,6,1,this,() -> fastFall.canDisplay() && fastFall.get() && wdFastFallMode.is("Predict"));
    private final BoolValue expand = new BoolValue("More Expand", false, this, () -> Objects.equals(mode.get(), "EntityCollide"));
    private final BoolValue ignoreDamage = new BoolValue("Ignore Damage", true, this, () -> Objects.equals(mode.get(), "EntityCollide"));
    private final BoolValue pullDown = new BoolValue("Pull Down", true, this, () -> Objects.equals(mode.get(), "NCP"));
    private final SliderValue onTick = new SliderValue("On Tick", 5, 1, 10, 1, this, () -> Objects.equals(mode.get(), "NCP") && pullDown.get());
    private final BoolValue onHurt = new BoolValue("On Hurt", true, this, () -> Objects.equals(mode.get(), "NCP") && pullDown.get());
    private final BoolValue airBoost = new BoolValue("Air Boost", true, this, () -> Objects.equals(mode.get(), "NCP"));
    private final BoolValue damageBoost = new BoolValue("Damage Boost", false, this, () -> Objects.equals(mode.get(), "NCP"));
    public final BoolValue noBob = new BoolValue("No Bob", true, this);
    private final BoolValue forceStop = new BoolValue("Force Stop", true, this);
    private final BoolValue lagBackCheck = new BoolValue("Lag Back Check", true, this);
    private final BoolValue liquidCheck = new BoolValue("Liquid Check", true, this);
    private final BoolValue debug = new BoolValue("Debug", true, this);
    private final BoolValue printOffGroundTicks = new BoolValue("Print Off Ground Ticks", true, this);
    private boolean disable;
    private boolean stopVelocity = false;
    public boolean couldStrafe = false;
    private double speed;
    private double print1;

    @Override
    public void onEnable() {
        if (mode.is("Watchdog")) {
            if (fastFall.canDisplay() && fastFall.get() || wdFastFallMode.is("Full Strafe") && !fullStrafeMode.is("A")) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    MovementUtils.strafe();
                }
            }
            if (wdMode.is("Glide")) {
                speed = 0.28;
            }
            disable = true;
        }
        print1 = 0;
    }

    @Override
    public void onDisable() {
        disable = false;
        couldStrafe = false;
        if(forceStop.get()){
            MovementUtils.strafe(0);
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());
        if(liquidCheck.get() && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()))
            return;

        if(printOffGroundTicks.get())
            DebugUtils.sendMessage(mc.thePlayer.offGroundTicks + "Tick");

        switch (mode.get()) {
            case "NCP": {
                if (mc.thePlayer.offGroundTicks == onTick.get() && pullDown.get()) {
                    MovementUtils.strafe();
                    mc.thePlayer.motionY -= 0.1523351824467155;
                }

                if (onHurt.get() && mc.thePlayer.hurtTime >= 5 && mc.thePlayer.motionY >= 0) {
                    mc.thePlayer.motionY -= 0.1;
                }

                if (airBoost.get() && MovementUtils.isMoving()) {
                    mc.thePlayer.motionX *= 1f + 0.00718;
                    mc.thePlayer.motionZ *= 1f + 0.00718;
                }
            }

            case "Intave": {
                if (mc.thePlayer.motionY > 0.03 && mc.thePlayer.isSprinting()) {
                    mc.thePlayer.motionX *= 1f + 0.003;
                    mc.thePlayer.motionZ *= 1f + 0.003;
                }
            }

            case "EntityCollide": {
                couldStrafe = false;
                if (mc.thePlayer.hurtTime <= 1) {
                    stopVelocity = false;
                }

                if (stopVelocity && !ignoreDamage.get()) {
                    return;
                }

                if (!MovementUtils.isMoving())
                    return;

                int collisions = 0;
                AxisAlignedBB box = expand.get() ? mc.thePlayer.getEntityBoundingBox().expand(1.0, 1.0, 1.0)
                        : mc.thePlayer.getEntityBoundingBox().expand(0.8, 0.8, 0.8);
                for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                    AxisAlignedBB entityBox = entity.getEntityBoundingBox();
                    if (canCauseSpeed(entity) && box.intersectsWith(entityBox)) {
                        collisions++;
                    }
                }

                double yaw = Math.toRadians(RotationUtils.shouldRotate() ? RotationUtils.currentRotation[0] : mc.thePlayer.rotationYaw);

                double boost = 0.078 * collisions;
                mc.thePlayer.addVelocity(-Math.sin(yaw) * boost, 0.0, Math.cos(yaw) * boost);
            }
            break;

            case "Watchdog":
                if (fastFall.canDisplay() && fastFall.get() && isEnabled(Disabler.class) && getModule(Disabler.class).options.isEnabled("Watchdog Motion") && !getModule(Disabler.class).disabled) {
                    if (!disable) {
                        switch (wdFastFallMode.get()) {
                            case "Normal":
                                switch (mc.thePlayer.offGroundTicks) {
                                    case 5:
                                        mc.thePlayer.motionY = -0.1523351824467155;
                                        break;
                                    case 8:
                                        mc.thePlayer.motionY = -0.3;
                                        break;
                                }
                                break;

                            case "Test 1":
                                switch (mc.thePlayer.offGroundTicks) {
                                    case 1:
                                        MovementUtils.strafe();
                                        couldStrafe = true;
                                        break;
                                    case 4:
                                        mc.thePlayer.motionY -= 0.03;
                                        break;
                                    case 5:
                                        mc.thePlayer.motionY -= 0.1905189780583944;
                                        break;
                                    case 6:
                                        mc.thePlayer.motionY *= 1.01;
                                        break;
                                }
                                break;

                            case "Test 2":
                                switch (mc.thePlayer.offGroundTicks) {
                                    case 4:
                                        mc.thePlayer.motionY -= 0.045;
                                        break;
                                    case 5:
                                        mc.thePlayer.motionY = -0.19;
                                        break;
                                    case 6:
                                        mc.thePlayer.motionY = -0.269;
                                        break;
                                    case 7:
                                        mc.thePlayer.motionY = -0.347;
                                        break;
                                }
                                break;

                            case "Test 3":
                                switch (mc.thePlayer.offGroundTicks) {
                                    case 3:
                                        mc.thePlayer.motionY -= -0.0045;
                                        break;
                                    case 4:
                                        mc.thePlayer.motionY -= 0.186;
                                        break;
                                    case 5:
                                        mc.thePlayer.motionY -= 0.042;
                                        break;
                                    case 7:
                                        mc.thePlayer.motionY += 0.012f;
                                        break;
                                    case 8:
                                        mc.thePlayer.motionY += 0.015f;
                                        break;
                                }
                                break;

                            case "Test 4":
                                switch (mc.thePlayer.offGroundTicks) {
                                    case 3:
                                        mc.thePlayer.motionY -= -0.0025;
                                        break;
                                    case 4:
                                        mc.thePlayer.motionY -= 0.04;
                                        break;
                                    case 5:
                                        mc.thePlayer.motionY -= 0.1905189780583944;
                                        break;
                                    case 6:
                                        mc.thePlayer.motionX *= 1.001;
                                        mc.thePlayer.motionZ *= 1.001;
                                    case 7:
                                        mc.thePlayer.motionY -= 0.004;
                                        break;
                                    case 8:
                                        mc.thePlayer.motionY -= 0.01;
                                        break;
                                }
                                break;
                        }
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {

        if(liquidCheck.get() && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()))
            return;

        if (isEnabled(Scaffold.class) && (getModule(Scaffold.class).towering() || getModule(Scaffold.class).towerMoving()))
            return;

        switch (mode.get()) {
            case "Intave": {
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    mc.gameSettings.keyBindJump.isPressed();
                }
            }

            case "NCP": {
                if (MovementUtils.isMoving()) {
                    couldStrafe = true;
                    MovementUtils.strafe();
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        MovementUtils.strafe(0.48 + MovementUtils.getSpeedEffect() * 0.07);
                    }
                }

                if (damageBoost.get() && mc.thePlayer.hurtTime > 0) {
                    MovementUtils.strafe(Math.max(MovementUtils.getSpeed(),0.5));
                }
            }

            case "Watchdog":
                if (mc.gameSettings.keyBindJump.isKeyDown())
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                if (event.isPre()) {
                    switch (wdMode.get()) {
                        case "Basic":
                            if (MovementUtils.isMoving()) {
                                if (mc.thePlayer.onGround) {
                                    float baseSpeed = 0.4825f;
                                    mc.thePlayer.jump();
                                    MovementUtils.strafe(baseSpeed + ((MovementUtils.getSpeedEffect() * 0.042f)));
                                }

                                if (boost.get()) {
                                    if (mc.thePlayer.offGroundTicks == 1)
                                        MovementUtils.strafe(MovementUtils.getBaseMoveSpeed());
                                }
                            }
                            break;

                        case "Custom":
                            if (MovementUtils.isMoving()) {
                                if (mc.thePlayer.onGround) {
                                    mc.thePlayer.jump();
                                    MovementUtils.strafe(0.43 + ((MovementUtils.getSpeedEffect() * 0.02f)));
                                }
                            }
                            break;

                        case "Full Strafe":
                            if (MovementUtils.isMoving()) {

                                switch (fullStrafeMode.get()) {

                                    case "A":

                                        if (mc.thePlayer.onGround) {
                                            mc.thePlayer.jump();
                                            MovementUtils.strafe(0.43 + ((MovementUtils.getSpeedEffect() * 0.02f)));
                                        }


                                        if (!disable) {
                                            int simpleY = (int) Math.round((event.y % 1) * 10000);
                                            if(debug.get())
                                                DebugUtils.sendMessage(simpleY + "Value");

                                            if (simpleY == 1661) {
                                                mc.thePlayer.motionY -= (mc.thePlayer.posY - mc.thePlayer.prevPosY) * 2;
                                            }

                                            //1661
                                            //2492
                                            //1691
                                        }

                                        break;
                                    case "B":
                                        if (mc.thePlayer.onGround) {
                                            mc.thePlayer.jump();
                                            MovementUtils.strafe(0.43 + ((MovementUtils.getSpeedEffect() * 0.02f)));
                                        }

                                        if (!disable) {
                                            int simpleY = (int) Math.round((event.y % 1) * 10000);

                                            if (simpleY == 13) {
                                                mc.thePlayer.motionY -= (mc.thePlayer.posY - mc.thePlayer.prevPosY);
                                            }
                                        }
                                        break;
                                }

                                if (mc.thePlayer.offGroundTicks <= 7 && mc.thePlayer.offGroundTicks != 0) {
                                    MovementUtils.strafe();
                                    couldStrafe = true;
                                }
                            }
                            break;
                    }

                    if (fastFall.canDisplay() && fastFall.get() || wdMode.is("Full Strafe")) {
                        if (PlayerUtils.blockRelativeToPlayer(0, mc.thePlayer.motionY, 0) != Blocks.air) {
                            disable = false;
                        }
                        if (mc.thePlayer.ticksSinceStep < 20 || PlayerUtils.isBlockOver(2)) {
                            disable = true;
                        }
                        if (!disable) {
                            event.setY(event.getY() + 1E-14);
                        }
                    }


                    if (fastFall.canDisplay() && fastFall.get() && isEnabled(Disabler.class) && getModule(Disabler.class).options.isEnabled("Watchdog Motion") && !getModule(Disabler.class).disabled) {

                        if (!disable &&
                                wdFastFallMode.is("8 Tick")) {
                            boolean down = false;
                            int simpleY = (int) Math.round((event.y % 1) * 10000);

                            if(debug.get())
                                DebugUtils.sendMessage(simpleY + "Value, " + event.y);

                            //0
                            //4200
                            //7532
                            //13
                            //1413
                            //2000
                            //1792
                            //804
                            //9051
                            //6550
                            //3315


                            if (simpleY == 13) {
                                mc.thePlayer.motionY = mc.thePlayer.motionY - 0.02483;
                            }

                            if (simpleY == 2000) {
                                mc.thePlayer.motionY = mc.thePlayer.motionY - 0.1913;
                            }

                            if (simpleY == 13) down = true;
                            if (down) event.y -= 1E-5;
                        }
                    }
                }

                break;
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {

        if(liquidCheck.get() && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()))
            return;

        if (mode.get().equals("Watchdog")) {
            if (wdMode.get().equals("Glide")) {
                if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance());
                    mc.thePlayer.jump();
                }

                if (mc.thePlayer.onGround) {
                    speed = 1.0F;
                }

                final int[] allowedAirTicks = new int[]{10, 11, 13, 14, 16, 17, 19, 20, 22, 23, 25, 26, 28, 29};

                if (!(mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(0, -0.25, 0)).getBlock() instanceof BlockAir)) {
                    for (final int allowedAirTick : allowedAirTicks) {
                        if (mc.thePlayer.offGroundTicks == allowedAirTick && allowedAirTick <= 11) {
                            mc.thePlayer.motionY = 0;
                            MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance() * speed);
                            couldStrafe = true;

                            speed *= 0.98F;

                        }
                    }
                }
            }

            if (fastFall.canDisplay() && fastFall.get() && isEnabled(Disabler.class) && getModule(Disabler.class).options.isEnabled("Watchdog Motion") && !getModule(Disabler.class).disabled) {
                switch (wdFastFallMode.get()) {
                    case "Test 5":
                        if (!disable) {
                            switch (mc.thePlayer.offGroundTicks) {
                                case 3:
                                    mc.thePlayer.motionY -= -0.0025;
                                    break;
                                case 4:
                                    mc.thePlayer.motionY -= 0.04;
                                    break;
                                case 5:
                                    mc.thePlayer.motionY -= 0.1905189780583944;
                                    break;
                                case 6:
                                    mc.thePlayer.motionX *= 1.001;
                                    mc.thePlayer.motionZ *= 1.001;
                                case 7:
                                    mc.thePlayer.motionY -= 0.004;
                                    break;
                                case 8:
                                    mc.thePlayer.motionY -= 0.01;
                            }
                        }
                        break;
                    case "Predict":
                        if (!disable) {
                            if (mc.thePlayer.offGroundTicks == predictTicks.get()) {
                                mc.thePlayer.motionY = MovementUtils.predictedMotionY(mc.thePlayer.motionY, 2);
                            }
                        }
                        break;
                    case "Predict 2":
                        if (!disable) {
                            if (mc.thePlayer.offGroundTicks == 5 || mc.thePlayer.offGroundTicks == 6) {
                                mc.thePlayer.motionY = MovementUtils.predictedMotionY(mc.thePlayer.motionY, 1);
                            }
                        }
                        break;
                }

                if (strafe.get()) {
                    if (mc.thePlayer.offGroundTicks == 1 || mc.thePlayer.offGroundTicks == 3 || (mc.thePlayer.offGroundTicks >= 7 && mc.thePlayer.offGroundTicks <= 8)) {
                        MovementUtils.strafe();
                        couldStrafe = true;
                    }
                }

                if (PlayerUtils.blockRelativeToPlayer(0, mc.thePlayer.motionY, 0) != Blocks.air && mc.thePlayer.offGroundTicks > 2) {
                    MovementUtils.strafe();
                    couldStrafe = true;
                }
            }
        }
    }

    @EventTarget
    public void onMove(MoveEvent event){

        if(liquidCheck.get() && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()))
            return;

    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (lagBackCheck.get() && event.getPacket() instanceof S08PacketPlayerPosLook)
            toggle();
    }

    private boolean canCauseSpeed(Entity entity) {
        return entity != mc.thePlayer && entity instanceof EntityLivingBase && !(entity instanceof EntityArmorStand);
    }
}
