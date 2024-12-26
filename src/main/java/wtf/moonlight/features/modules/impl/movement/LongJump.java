package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.apache.commons.lang3.Range;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.StrafeEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.player.MovementUtils;

import java.util.Objects;

import static java.lang.System.in;

@ModuleInfo(name = "LongJump", category = ModuleCategory.Movement, key = Keyboard.KEY_F)
public class LongJump extends Module {
    public final ModeValue mode = new ModeValue("Mode", new String[]{"Watchdog Fireball", "Old Matrix", "Miniblox"}, "Watchdog Fireball", this);
    public final ModeValue wdFBMode = new ModeValue("Fireball Mode", new String[]{"Rise", "Chef", "Chef High"}, "Watchdog Fireball", this);
    private final SliderValue oMatrixTimer = new SliderValue("Matrix Timer", 0.3f, 0.1f, 1, 0.01f, this, () -> mode.is("Old Matrix"));
    private final BoolValue boost = new BoolValue("Boost", true, this, () -> mode.is("Watchdog Fireball"));
    private int lastSlot = -1;
    //fb
    private int ticks = -1;
    private boolean setSpeed;
    private int ticksSinceVelocity;
    public static boolean stopModules;
    private boolean sentPlace;
    private int initTicks;
    private boolean thrown;
    private boolean velo;

    //matrix
    private boolean mPacket;
    private int matrixTimer = 0;

    //miniblox
    private boolean jumped;
    private int currentTimer = 0;
    private int pauseTimes = 0;

    //others
    private double distance;

    @Override
    public void onEnable() {
        lastSlot = mc.thePlayer.inventory.currentItem;
        ticks = 0;
        distance = 0;
        if (mode.is("Watchdog Fireball")) {
            int fbSlot = getFBSlot();
            if (fbSlot == -1) {
                toggle();
            }

            stopModules = true;
            initTicks = 0;
        }
    }

    @Override
    public void onDisable() {
        if (Objects.equals(mode.get(), "Watchdog Fireball")) {
            if (lastSlot != -1) {
                mc.thePlayer.inventory.currentItem = lastSlot;
            }


            ticks = lastSlot = -1;
            setSpeed = stopModules = sentPlace = false;
            initTicks = 0;
            ticksSinceVelocity = 0;
            velo = false;
        }

        if (Objects.equals(mode.get(), "Old Matrix")) {
            mPacket = false;
            matrixTimer = 0;
            mc.timer.timerSpeed = 1f;
        }

        if (Objects.equals(mode.get(), "Miniblox")) {
            jumped = false;
            currentTimer = 0;
            pauseTimes = 0;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());
        if (velo)
            ticksSinceVelocity++;
        switch (mode.get()) {
            case "Old Matrix":
                if (!mPacket) {
                    if (mc.thePlayer.onGround)
                        mc.thePlayer.jump();
                    sendPacketNoEvent(new C03PacketPlayer(false));
                    mPacket = true;
                }
                if (mPacket) {
                    mc.timer.timerSpeed = oMatrixTimer.get();
                    mc.thePlayer.motionX = 1.97 * -Math.sin(MovementUtils.getDirection());
                    mc.thePlayer.motionZ = 1.97 * Math.cos(MovementUtils.getDirection());
                    mc.thePlayer.motionY = 0.42;
                    matrixTimer++;

                    if (matrixTimer >= 3) {
                        toggle();
                    }
                }
                break;

            case "Miniblox":
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);

                if (!jumped) {
                    if (mc.thePlayer.onGround) mc.thePlayer.jump();
                    jumped = true;
                }

                if (jumped) {
                    mc.thePlayer.motionX = 1.95 * -Math.sin(MovementUtils.getDirection());
                    mc.thePlayer.motionZ = 1.95 * Math.cos(MovementUtils.getDirection());
                    mc.thePlayer.motionY = 0.42;
                    currentTimer++;

                    if (Range.between(4, 10).contains(currentTimer)) {
                        MovementUtils.stop();
                    } else if (currentTimer > 10) {
                        pauseTimes++;
                        currentTimer = 0;
                        jumped = false;
                    }
                }

                if (pauseTimes >= 2) {
                    MovementUtils.stop();
                    toggle();
                }
                break;
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {

        if (event.isPost()) {
            distance += Math.hypot(mc.thePlayer.posX - mc.thePlayer.lastTickPosX, mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ);
        }
        switch (mode.get()) {
            case "Watchdog Fireball":
                if (event.isPre()) {

                    if (velo && mc.thePlayer.onGround) {
                        toggle();
                    }

                    switch (wdFBMode.get()) {
                        case "Rise":
                            if (mc.thePlayer.hurtTime == 10) {
                                mc.thePlayer.motionY = 1.1f;
                            }

                            if (ticksSinceVelocity <= 80 && ticksSinceVelocity >= 1) {
                                mc.thePlayer.motionY += 0.028f;
                            }

                            if (ticksSinceVelocity == 28) {
                                if (boost.get()) {
                                    MovementUtils.strafe(0.42);
                                }
                                mc.thePlayer.motionY = 0.16f;
                            }
                            if (ticksSinceVelocity >= 35 && ticksSinceVelocity <= 50) {
                                MovementUtils.strafe();
                                mc.thePlayer.posY = mc.thePlayer.posY + .029f;

                            }

                            if (ticksSinceVelocity >= 3 && ticksSinceVelocity <= 50) {
                                MovementUtils.strafe();
                            }
                            break;
                        case "Chef":
                            if (velo) {
                                if (ticksSinceVelocity >= 1 && ticksSinceVelocity <= 33) {
                                    mc.thePlayer.motionY = 0.7 - ticksSinceVelocity * 0.015;
                                }
                            }
                            break;
                        case "Chef high":
                            if (velo) {
                                if (ticksSinceVelocity >= 1 && ticksSinceVelocity <= 28) {
                                    mc.thePlayer.motionY = ticksSinceVelocity * 0.016;
                                }
                            }
                            break;
                    }

                    if (initTicks == 0) {

                        event.setYaw(mc.thePlayer.rotationYaw - 180);
                        event.setPitch(89);
                        int fireballSlot = getFBSlot();
                        if (fireballSlot != -1 && fireballSlot != mc.thePlayer.inventory.currentItem) {
                            mc.thePlayer.inventory.currentItem = fireballSlot;
                        }
                    }
                    if (initTicks == 1) {

                        if (!sentPlace) {
                            sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            sentPlace = true;

                        }
                    } else if (initTicks == 2) {

                        if (lastSlot != -1) {
                            mc.thePlayer.inventory.currentItem = lastSlot;
                            lastSlot = -1;
                        }
                    }
                    if (setSpeed) {

                        stopModules = true;
                        MovementUtils.strafe(1.768f);
                        ticks++;
                    }
                    if (initTicks < 3) {
                        initTicks++;
                    }

                    if (setSpeed) {
                        if (ticks > 1) {
                            stopModules = setSpeed = false;
                            ticks = 0;
                            return;
                        }
                        stopModules = true;
                        ticks++;
                        MovementUtils.strafe(1.768f);
                    }
                }

                break;
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        switch (mode.get()) {

            case "Watchdog Fireball":
                if (ticksSinceVelocity <= 70 && ticksSinceVelocity >= 1) {
                    mc.thePlayer.motionX *= 1.0003;
                    mc.thePlayer.motionZ *= 1.0003;
                }

                if (ticksSinceVelocity == 1) {
                    mc.thePlayer.motionX *= 1.15;
                    mc.thePlayer.motionZ *= 1.15;
                }


                if (mc.thePlayer.hurtTime == 8) {
                    mc.thePlayer.motionX *= 1.02;
                    mc.thePlayer.motionZ *= 1.02;
                }

                if (mc.thePlayer.hurtTime == 7) {
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if (mc.thePlayer.hurtTime == 6) {
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if (mc.thePlayer.hurtTime == 5) {
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if (mc.thePlayer.hurtTime <= 4 && !(mc.thePlayer.hurtTime == 0)) {
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (mode.is("Watchdog Fireball")) {
            if (packet instanceof C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement && c08PacketPlayerBlockPlacement.getStack() != null && c08PacketPlayerBlockPlacement.getStack().getItem() instanceof ItemFireball) {
                thrown = true;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
            }

            if (packet instanceof S12PacketEntityVelocity s12PacketEntityVelocity) {
                if (s12PacketEntityVelocity.getEntityID() == mc.thePlayer.getEntityId()) {
                    if (thrown) {
                        ticksSinceVelocity = 0;
                        ticks = 0;
                        setSpeed = true;
                        thrown = false;
                        stopModules = true;
                        velo = true;
                    }
                }
            }
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        Fonts.interSemiBold.get(15).drawCenteredString((Math.round(distance * 100.0) / 100.0) + "blocks", (float) event.getScaledResolution().getScaledWidth() / 2, (float) event.getScaledResolution().getScaledHeight() / 2 - 30, -1);
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        setEnabled(false);
    }

    private int getFBSlot() {
        for (int i = 36; i <= 44; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemFireball) {
                return i - 36;
            }
        }
        return -1;
    }
}