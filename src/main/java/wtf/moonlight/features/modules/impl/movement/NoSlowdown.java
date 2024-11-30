package wtf.moonlight.features.modules.impl.movement;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.SlowDownEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.combat.AutoGap;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;

import java.util.Objects;

import static net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM;

@ModuleInfo(name = "NoSlowdown", category = ModuleCategory.Movement)
public class NoSlowdown extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Vanilla", "GrimAC", "Intave", "Old Intave", "Watchdog", "NCP"}, "Vanilla", this);
    private final BoolValue sprint = new BoolValue("Sprint", true, this);
    private boolean eat = true;
    private int lastFoodAmount;
    private float foodSpeed;
    private boolean send = false;
    private boolean ncpShouldWork = true;

    @EventTarget
    public void onMotion(MotionEvent event) {
        setTag(mode.get());

        switch (mode.get()) {
            case "Old Intave":
                if (isUsingConsumable() && event.isPre()) {
                    sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                    sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                break;

            case "NCP":
                if (mc.thePlayer.isUsingItem() && ncpShouldWork) {
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0, 0, 0));
                    }
                }
                break;

            case "GrimAC":
                if (event.isPost()) {
                    if (isUsingSword() || isUsingBow()) {
                        PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                        useItem.write(Type.VAR_INT, 1);
                        com.viaversion.viarewind.utils.PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                    }
                }

                if (mc.thePlayer.getHeldItem() != null) {
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemAppleGold && !isEnabled(AutoGap.class)) {
                        if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                            if (eat) {
                                if (mc.thePlayer.getHeldItem().stackSize <= 1) {
                                    return;
                                }
                                lastFoodAmount = mc.thePlayer.getHeldItem().stackSize;
                                boolean anti = true;
                                MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                                if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && isInteractBlock(mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock())) {
                                        anti = false;
                                    }
                                }
                                if (mc.thePlayer.getHeldItem().stackSize <= 1) {
                                    anti = false;
                                }
                                if (anti) {
                                    foodSpeed = 1;
                                    sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                                    sendPacket(new C0FPacketConfirmTransaction());
                                } else {
                                    foodSpeed = 0.2f;
                                }
                                eat = false;
                            }

                            if (mc.gameSettings.keyBindUseItem.isKeyDown() && !eat && mc.thePlayer.getHeldItem().stackSize != this.lastFoodAmount) {
                                foodSpeed = 0.2f;
                                eat = true;
                            }
                        }
                        if (!mc.gameSettings.keyBindUseItem.isKeyDown() && !eat) {
                            eat = true;
                            this.lastFoodAmount = 0;
                        }
                    }
                }
                break;

            case "Intave":
                if (isUsingConsumable() && event.isPre())
                    sendPacketNoEvent(new C07PacketPlayerDigging(RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
                break;

            case "Watchdog":
                if (isHoldingConsumable()) {
                    if (mc.thePlayer.offGroundTicks == 4 && send) {
                        send = false;
                        sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));

                    } else if (mc.thePlayer.isUsingItem()) {
                        event.setY(event.getY() + 1E-14);
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet packet = event.getPacket();
        switch (mode.get()) {
            case "NCP": {
                ncpShouldWork = !(packet instanceof C07PacketPlayerDigging);
            }

            case "GrimAC":
                if (!isEnabled(AutoGap.class)) {
                    if (mc.thePlayer.isUsingItem()) {
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemAppleGold) {
                            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                                if (packet instanceof C07PacketPlayerDigging) {
                                    if (((C07PacketPlayerDigging) packet).getStatus() == C07PacketPlayerDigging.Action.DROP_ITEM) {
                                        event.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                    boolean anti = true;
                    MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                    if (movingObjectPosition == null) return;
                    if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && isInteractBlock(mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock())) {
                            anti = false;
                        }
                    }
                    if (mc.thePlayer.getHeldItem().stackSize <= 1) {
                        anti = false;
                    }
                    if (mc.thePlayer.getHeldItem().getItem() instanceof ItemAppleGold) {
                        if (packet instanceof S2FPacketSetSlot && anti) {
                            event.setCancelled(true);
                        }
                    }
                }
                break;

            case "Watchdog":
                if (event.getState() == PacketEvent.State.OUTGOING)
                    if (packet instanceof C08PacketPlayerBlockPlacement blockPlacement && !mc.thePlayer.isUsingItem()) {
                        if (blockPlacement.getPlacedBlockDirection() == 255 && isHoldingConsumable() && mc.thePlayer.offGroundTicks < 2) {
                            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                                mc.thePlayer.jump();
                            }
                            send = true;
                            event.setCancelled(true);
                        }
                    }
                break;
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (Objects.equals(mode.get(), "GrimAC")) {
            if (eat) {
                foodSpeed = 0.2f;
            }
            if (mc.gameSettings.keyBindUseItem.isKeyDown() && !eat && mc.thePlayer.getHeldItem().stackSize != this.lastFoodAmount) {
                foodSpeed = 0.2f;
            }
        }
    }

    public boolean isHoldingConsumable() {
        return mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && !ItemPotion.isSplash(mc.thePlayer.getHeldItem().getMetadata()) || mc.thePlayer.getHeldItem().getItem() instanceof ItemBucketMilk;
    }

    public boolean isUsingConsumable() {
        return mc.thePlayer.isUsingItem() && isHoldingConsumable();
    }

    private boolean isUsingSword() {
        return mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private boolean isUsingBow() {
        return mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow;
    }

    private boolean isInteractBlock(Block block) {
        return block instanceof BlockFence ||
                block instanceof BlockFenceGate ||
                block instanceof BlockDoor ||
                block instanceof BlockChest ||
                block instanceof BlockEnderChest ||
                block instanceof BlockEnchantmentTable ||
                block instanceof BlockFurnace ||
                block instanceof BlockAnvil ||
                block instanceof BlockBed ||
                block instanceof BlockWorkbench ||
                block instanceof BlockNote ||
                block instanceof BlockTrapDoor ||
                block instanceof BlockHopper ||
                block instanceof BlockDispenser ||
                block instanceof BlockDaylightDetector ||
                block instanceof BlockRedstoneRepeater ||
                block instanceof BlockRedstoneComparator ||
                block instanceof BlockButton ||
                block instanceof BlockBeacon ||
                block instanceof BlockBrewingStand ||
                block instanceof BlockSign;
    }

    @EventTarget
    public void onSlowDown(SlowDownEvent event) {
        if (mode.is("GrimAC") && isUsingConsumable()//&& mc.thePlayer.inventory.getItemStack().stackSize <= 1
        ) {
            event.setForward(foodSpeed);
            event.setStrafe(foodSpeed);
            return;
        }
        event.setSprinting(sprint.get());

        event.setForward(1);
        event.setStrafe(1);
    }
}