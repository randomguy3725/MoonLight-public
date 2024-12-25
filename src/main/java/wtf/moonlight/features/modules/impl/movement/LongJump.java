package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
import wtf.moonlight.gui.notification.NotificationType;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.RotationUtils;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(name = "LongJump", category = ModuleCategory.Movement, key = Keyboard.KEY_F)
public class LongJump extends Module {
    public final ModeValue mode = new ModeValue("Mode", new String[]{"Watchdog Fireball", "Watchdog","Old Matrix"}, "Watchdog Fireball", this);
    public final ModeValue wdFBMode = new ModeValue("Fireball Mode", new String[]{"Rise","Chef","Chef High"}, "Watchdog Fireball", this);
    private final SliderValue wdSpeed = new SliderValue("Watchdog Speed", 0.5f, 0.5f, 1, 0.01f, this, () -> mode.is("Watchdog"));
    private final BoolValue detectSpeedPot = new BoolValue("Detect Speed Pot Boost", true,this, () -> mode.is("Watchdog"));
    private final SliderValue oMatrixTimer = new SliderValue("Matrix Timer", 0.3f, 0.1f, 1, 0.01f, this, () -> mode.is("Old Matrix"));
    private final BoolValue boost = new BoolValue("Boost",true,this, () -> mode.is("Watchdog Fireball"));
    private int lastSlot = -1;
    private long lastPlayerTick = 0;
    //fb
    private int ticks = -1;
    private boolean setSpeed;
    private int ticksSinceVelocity;
    public static boolean stopModules;
    private boolean sentPlace;
    private int initTicks;
    private boolean thrown;
    //bow
    private int bowState = 0;
    private final Queue<Packet<?>> delayedPackets = new ConcurrentLinkedQueue<>();

    //matrix
    private boolean packet;
    private int matrixTimer = 0;
    //others
    private double distance;

    @Override
    public void onEnable() {
        lastSlot = mc.thePlayer.inventory.currentItem;
        ticks = 0;
        lastPlayerTick = -1;
        distance = 0;
        if (mode.is("Watchdog Fireball")) {
            int fbSlot = getFBSlot();
            if (fbSlot == -1) {
                setEnabled(false);
            }

            stopModules = true;
            initTicks = 0;
        }

        if (mode.is("Watchdog")) {
            bowState = 0;
        }
    }

    @Override
    public void onDisable() {
        if (Objects.equals(mode.get(), "Watchdog Fireball")) {
            MovementUtils.stop();
            if (lastSlot != -1) {
                mc.thePlayer.inventory.currentItem = lastSlot;
            }


            ticks = lastSlot = -1;
            setSpeed = stopModules = sentPlace = false;
            initTicks = 0;
            ticksSinceVelocity = 0;
        }
        if (Objects.equals(mode.get(), "Old Matrix")) {
            packet = false;
            matrixTimer = 0;
            mc.timer.timerSpeed = 1f;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());
        ticksSinceVelocity++;
        switch (mode.get()) {
            case "Old Matrix":
                if (!packet) {
                    if (mc.thePlayer.onGround)
                        mc.thePlayer.jump();
                    sendPacketNoEvent(new C03PacketPlayer(false));
                    packet = true;
                }
                if (packet) {
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
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {

        if (event.isPost()) {
            distance += Math.hypot(mc.thePlayer.posX - mc.thePlayer.lastTickPosX, mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ);
        }
        switch (mode.get()) {
            case "Watchdog Fireball":
                if(event.isPre()){

                    if(thrown && mc.thePlayer.onGround){
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
                            if (thrown) {
                                if (ticksSinceVelocity >= 1 && ticksSinceVelocity <= 33) {
                                    mc.thePlayer.motionY = 0.7 - ticksSinceVelocity * 0.015;
                                }
                            }
                            break;
                        case "Chef high":
                            if (thrown) {
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
                            lastSlot = mc.thePlayer.inventory.currentItem;
                            mc.thePlayer.inventory.currentItem = fireballSlot;
                        }
                    }
                    if (initTicks == 1) {

                        if (!sentPlace) {
                            sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            sentPlace = true;

                        }
                    } else if (initTicks == 2) {

                        if (lastSlot != -1) {
                            mc.thePlayer.inventory.currentItem = lastSlot;
                            lastSlot = -1;
                        }
                    }
                    if (ticks > 1) {
                        this.toggle();
                        return;
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

            case "Watchdog":
                if (bowState < 2) {
                    if (mc.thePlayer.onGround)
                        MovementUtils.stopXZ();
                }
                switch (bowState) {
                    case 1:
                        RotationUtils.setRotation(new float[]{mc.thePlayer.rotationYaw,-90});
                        break;
                }

                if (bowState == 5)
                    setEnabled(false);

                break;
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        switch (mode.get()) {

            case "Watchdog":
                if (bowState < 2) {
                    event.setForward(0);
                    event.setStrafe(0);
                }
                switch (bowState) {
                    case 0:
                        int slot = getBowSlot();
                        if (slot < 0 || !mc.thePlayer.inventory.hasItem(Items.arrow)) {
                            INSTANCE.getNotificationManager().post(NotificationType.WARNING, "No arrows or bow found in your inventory!", "Disabling LongJump", 2);
                            bowState = 5;
                            break; // nothing to shoot
                        } else if (lastPlayerTick == -1) {

                            if (lastSlot != slot) sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                            sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventoryContainer.getSlot(slot + 36).getStack(), 0, 0, 0));

                            lastPlayerTick = mc.thePlayer.ticksExisted;
                            bowState = 1;
                        }
                        break;
                    case 1:
                        int reSlot = getBowSlot();
                        if (mc.thePlayer.ticksExisted - lastPlayerTick > 2) {
                            sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));

                            if (lastSlot != reSlot)
                                sendPacketNoEvent(new C09PacketHeldItemChange(lastSlot));
                        }
                        break;
                    case 2:
                        if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.onGround) {
                            MovementUtils.strafe(MovementUtils.getAllowedHorizontalDistance());
                            mc.thePlayer.jump();
                            bowState = 3;
                        }
                        break;
                    case 3:
                        if (mc.thePlayer.offGroundTicks >= 7) {
                            synchronized (delayedPackets) {
                                for (Packet p : delayedPackets) {
                                    p.processPacket(mc.getNetHandler());
                                }
                                delayedPackets.clear();
                            }
                            bowState = 4;
                        }
                        break;
                    case 4:
                        MovementUtils.strafe(wdSpeed.get() + (detectSpeedPot.get() ? MovementUtils.getSpeedEffect() * 0.0575f : 0));
                        bowState = 5;
                        break;
                }
                break;

            case "Watchdog Fireball":
                if (ticksSinceVelocity <= 70 && ticksSinceVelocity >= 1) {
                    mc.thePlayer.motionX *= 1.0003;
                    mc.thePlayer.motionZ *= 1.0003;
                }

                if (ticksSinceVelocity == 1) {
                    mc.thePlayer.motionX *= 1.15;
                    mc.thePlayer.motionZ *= 1.15;
                }


                if (mc.thePlayer.hurtTime == 8){
                    mc.thePlayer.motionX *= 1.02;
                    mc.thePlayer.motionZ *= 1.02;
                }

                if (mc.thePlayer.hurtTime == 7){
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if (mc.thePlayer.hurtTime == 6){
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if (mc.thePlayer.hurtTime == 5){
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if (mc.thePlayer.hurtTime <= 4 && !(mc.thePlayer.hurtTime == 0)){
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity s12PacketEntityVelocity) {
            if (s12PacketEntityVelocity.getEntityID() == mc.thePlayer.getEntityId()) {
                ticksSinceVelocity = 0;
            }
        }
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
                        ticks = 0;
                        setSpeed = true;
                        thrown = false;
                        stopModules = true;
                    }
                }
            }
        }
        if (mode.is("Watchdog")) {
            if (event.getState() == PacketEvent.State.INCOMING) {
                if (packet instanceof S12PacketEntityVelocity s12) {
                    if (s12.getEntityID() == mc.thePlayer.getEntityId() && (bowState == 1 || bowState == 2)) {
                        delayedPackets.add(packet);
                        bowState = 2;
                    } else if (event.getPacket() instanceof S32PacketConfirmTransaction && (bowState == 1 || bowState == 2)) {
                        delayedPackets.add(packet);
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

    private int getBowSlot() {
        for (int i = 36; i <= 44; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBow) {
                return i - 36;
            }
        }
        return -1;
    }
}
