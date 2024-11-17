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
    public final ModeValue mode = new ModeValue("Mode", new String[]{"Fireball", "Watchdog","Old Matrix"}, "Fireball", this);
    private final ModeValue fbMode = new ModeValue("FB Mode", new String[]{"Boost Only", "Flat", "Opal", "Chef", "Chef High"}, "Opal", this, () -> mode.is("Fireball"));
    private final SliderValue wdSpeed = new SliderValue("Watchdog Speed", 0.5f, 0.5f, 1, 0.01f, this, () -> mode.is("Watchdog"));
    private final BoolValue detectSpeedPot = new BoolValue("Detect Speed Pot Boost", true,this, () -> mode.is("Watchdog"));
    private final SliderValue oMatrixTimer = new SliderValue("Matrix Timer", 0.3f, 0.1f, 1, 0.01f, this, () -> mode.is("Old Matrix"));
    private int lastSlot = -1;
    private long lastPlayerTick = 0;
    //fb
    private int ticks;
    private boolean sentPlace;
    private boolean start;
    private int initTicks;
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
        if (mode.is("Fireball")) {
            int fbSlot = getFBSlot();
            if (fbSlot == -1) {
                setEnabled(false);
            } else {
                mc.thePlayer.inventory.currentItem = fbSlot;
                mc.playerController.updateController();
            }
            initTicks = 0;
        }

        if (mode.is("Watchdog")) {
            bowState = 0;
        }
    }

    @Override
    public void onDisable() {
        if (Objects.equals(mode.get(), "Fireball")) {
            lastSlot = -1;
            sentPlace = false;
            initTicks = 0;
            start = false;
            ticks = 0;
            distance = 0;
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
        switch (mode.get()) {
            case "Fireball":

                if (mc.thePlayer.hurtTime >= 3) {
                    start = true;
                }
                if (start && sentPlace) {
                    ticks++;
                }

                switch (fbMode.get()) {
                    case "Flat":
                        if (ticks > 0 && ticks < 30) {
                            mc.thePlayer.motionY = 0.01f;
                        } else if (ticks >= 31) {
                            setEnabled(false);
                        }
                        break;
                    case "Opal":
                        if (ticks == 1) {
                            mc.thePlayer.motionY = mc.thePlayer.motionY + 0.061;
                        } else {
                            mc.thePlayer.motionY = mc.thePlayer.motionY + 0.0283;
                        }
                        break;

                    case "Chef":
                        if (ticks >= 1 && ticks <= 33) {
                            mc.thePlayer.motionY = 0.7 - ticks * 0.015;
                        } else if (ticks > 1) {
                            setEnabled(false);
                        }
                        break;

                    case "Chef high":
                        if (ticks >= 1 && ticks <= 28) {
                            mc.thePlayer.motionY = ticks * 0.016;
                        } else if (ticks > 1) {
                            setEnabled(false);
                        }
                        break;
                }

                if (mc.thePlayer.onGround && ticks > 5) {
                    setEnabled(false);
                }

                if (start && mc.thePlayer.hurtTime == 9 && mc.gameSettings.keyBindForward.isKeyDown()) {
                    if (fbMode.is("Boost Only") || fbMode.is("Opal")) {
                        MovementUtils.strafe(1.94);
                    } else if (fbMode.is("Flat")) {
                        MovementUtils.strafe(1.6);
                    } else if (fbMode.is("Chef") || fbMode.is("Chef High")) {
                        MovementUtils.strafe(1.75);
                    }
                }

                if (fbMode.is("Flat") && start && mc.thePlayer.hurtTime == 8 && mc.gameSettings.keyBindForward.isKeyDown()) {
                    MovementUtils.strafe(1.8);
                }
                break;

            case "Old Matrix":
                if(!packet) {
                    if(mc.thePlayer.onGround)
                        mc.thePlayer.jump();
                    sendPacketNoEvent(new C03PacketPlayer(false));
                    packet = true;
                }
                if(packet) {
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
            case "Fireball":
                switch (initTicks) {
                    case 0:
                        int fireballSlot = getFBSlot();
                        if (fireballSlot != -1 && fireballSlot != mc.thePlayer.inventory.currentItem) {
                            lastSlot = mc.thePlayer.inventory.currentItem;
                            mc.thePlayer.inventory.currentItem = (fireballSlot);
                        }
                    case 1:
                        RotationUtils.setRotation(new float[]{mc.thePlayer.rotationYaw - 180,89});
                        break;
                    case 2:
                        if (!sentPlace) {
                            sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            sentPlace = true;
                        }
                        break;
                    case 3:
                        if (lastSlot != -1) {
                            mc.thePlayer.inventory.currentItem = (lastSlot);
                            lastSlot = -1;
                        }
                        break;
                }

                if (initTicks <= 3) {
                    initTicks++;
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
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (mode.is("Fireball")) {
            if (event.getState() == PacketEvent.State.OUTGOING) {
                if (packet instanceof C08PacketPlayerBlockPlacement
                        && ((C08PacketPlayerBlockPlacement) event.getPacket()).getStack() != null
                        && ((C08PacketPlayerBlockPlacement) event.getPacket()).getStack().getItem() instanceof ItemFireball) {
                    if (mc.thePlayer.onGround && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && (fbMode.is("Chef") || fbMode.is("Chef High"))) {
                        mc.thePlayer.jump();
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
