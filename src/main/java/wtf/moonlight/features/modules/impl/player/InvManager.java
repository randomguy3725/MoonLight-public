package wtf.moonlight.features.modules.impl.player;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.player.InventoryUtils;

import java.util.*;

@ModuleInfo(name = "InvManager", category = ModuleCategory.Player, key = Keyboard.KEY_L)
public class InvManager extends Module {
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Open Inventory", "Spoof"}, "Open Inventory", this);
    private final SliderValue minDelay = new SliderValue("Min Delay", 1, 0, 5, 1, this);
    private final SliderValue maxDelay = new SliderValue("Max Delay", 1, 0, 5, 1, this);
    private final BoolValue dropItems = new BoolValue("Drop Items", true, this);
    private final BoolValue sortItems = new BoolValue("Sort Items", true, this);
    private final BoolValue autoArmor = new BoolValue("Auto Armor", true, this);
    private final BoolValue startDelay = new BoolValue("Start Delay", true, this);
    public final BoolValue display = new BoolValue("Display", true, this);
    private final TimerUtils timer = new TimerUtils();
    private final int[] bestArmorPieces = new int[4];
    private final List<Integer> trash = new ArrayList<>();
    private final int[] bestToolSlots = new int[3];
    private final List<Integer> gappleStackSlots = new ArrayList<>();
    private final List<Integer> blockSlot = new ArrayList<>();
    private int bestSwordSlot;
    private int bestBowSlot;
    private boolean serverOpen;
    private boolean clientOpen;
    private boolean nextTickCloseInventory;
    public int slot;

    @EventTarget
    public void onPacketSend(PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C16PacketClientStatus clientStatus) {

            if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                if(startDelay.get())
                    timer.reset();
                this.clientOpen = true;
                this.serverOpen = true;
            }
        } else if (packet instanceof C0DPacketCloseWindow packetCloseWindow) {

            if (packetCloseWindow.windowId == mc.thePlayer.inventoryContainer.windowId) {
                this.clientOpen = false;
                this.serverOpen = false;
                slot = -1;
            }
        }
        if (packet instanceof S2DPacketOpenWindow) {
            this.clientOpen = false;
            this.serverOpen = false;
        }
    }

    private boolean dropItem(final List<Integer> listOfSlots) {
        if (this.dropItems.get()) {
            if (!listOfSlots.isEmpty()) {
                int slot = listOfSlots.remove(0);
                windowClick(slot, 1, 4);
                return true;
            }
        }
        return false;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(String.valueOf(MathUtils.nextInt((int) minDelay.get(), (int) maxDelay.get())));
        final long delay = (MathUtils.nextInt((int) minDelay.get(), (int) maxDelay.get()) * 50L);
        if (this.clientOpen || (mc.currentScreen == null && !Objects.equals(this.mode.get(), "Open Inventory"))) {
            if ((this.timer.hasTimeElapsed(delay) || MathUtils.nextInt((int) minDelay.get(), (int) maxDelay.get()) == 0)) {
                this.clear();

                for (int slot = InventoryUtils.INCLUDE_ARMOR_BEGIN; slot < InventoryUtils.END; slot++) {
                    final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();

                    if (stack != null) {
                        // Find Best Sword
                        if (stack.getItem() instanceof ItemSword && InventoryUtils.isBestSword(stack)) {
                            this.bestSwordSlot = slot;
                        }
                        //Find Best Bow
                        else if (stack.getItem() instanceof ItemBow && InventoryUtils.isBestBow(stack)) {
                            this.bestBowSlot = slot;
                        }
                        // Find Best Tools
                        else if (stack.getItem() instanceof ItemTool && InventoryUtils.isBestTool(mc.thePlayer, stack)) {
                            final int toolType = InventoryUtils.getToolType(stack);
                            if (toolType != -1 && slot != this.bestToolSlots[toolType])
                                this.bestToolSlots[toolType] = slot;
                        }
                        // Find Best Armor
                        else if (stack.getItem() instanceof ItemArmor armor && InventoryUtils.isBestArmor(mc.thePlayer, stack)) {

                            final int pieceSlot = this.bestArmorPieces[armor.armorType];

                            if (pieceSlot == -1 || slot != pieceSlot)
                                this.bestArmorPieces[armor.armorType] = slot;
                        } else if (stack.getItem() instanceof ItemBlock && slot == InventoryUtils.findBestBlockStack()) {
                            this.blockSlot.add(slot);
                        } else if (stack.getItem() instanceof ItemAppleGold) {
                            this.gappleStackSlots.add(slot);
                        } else if (!this.trash.contains(slot) && !InventoryUtils.isValidStack(stack)) {
                            this.trash.add(slot);
                        }
                    }
                }

                final boolean busy = (!this.trash.isEmpty() && this.dropItems.get()) || this.equipArmor(false) || this.sortItems(false);

                if (!busy) {
                    if (this.nextTickCloseInventory) {
                        this.close();
                        this.nextTickCloseInventory = false;
                    } else {
                        this.nextTickCloseInventory = true;
                    }
                    return;
                } else {
                    boolean waitUntilNextTick = !this.serverOpen;

                    this.open();

                    if (this.nextTickCloseInventory)
                        this.nextTickCloseInventory = false;

                    if (waitUntilNextTick) return;
                }

                if (this.equipArmor(true)) return;
                if (this.dropItem(this.trash)) return;
                this.sortItems(true);
                timer.reset();
            }
        }
    }

    private boolean sortItems(final boolean moveItems) {
        if (this.sortItems.get()) {

            if (this.bestSwordSlot != -1) {
                if (this.bestSwordSlot != 36) {
                    if (moveItems) {
                        this.putItemInSlot(36, this.bestSwordSlot);
                        this.bestSwordSlot = 36;
                    }
                    return true;
                }
            }

            if (this.bestBowSlot != -1) {
                if (this.bestBowSlot != 38) {
                    if (moveItems) {
                        this.putItemInSlot(38, this.bestBowSlot);
                        this.bestBowSlot = 38;
                    }
                    return true;
                }
            }

            if (!this.gappleStackSlots.isEmpty()) {
                this.gappleStackSlots.sort(Comparator.comparingInt(slot -> mc.thePlayer.inventoryContainer.getSlot(slot).getStack().stackSize));

                final int bestGappleSlot = this.gappleStackSlots.get(0);

                if (bestGappleSlot != 37) {
                    if (moveItems) {
                        this.putItemInSlot(37, bestGappleSlot);
                        this.gappleStackSlots.set(0, 37);
                    }
                    return true;
                }
            }

            if (!this.blockSlot.isEmpty()) {
                this.blockSlot.sort(Comparator.comparingInt(slot -> -mc.thePlayer.inventoryContainer.getSlot(slot).getStack().stackSize));

                final int blockSlot = this.blockSlot.get(0);

                if (blockSlot != 42) {
                    if (moveItems) {
                        this.putItemInSlot(42, blockSlot);
                        this.blockSlot.set(0, 42);
                    }
                    return true;
                }
            }

            final int[] toolSlots = {39, 40, 41};

            for (final int toolSlot : this.bestToolSlots) {
                if (toolSlot != -1) {
                    final int type = InventoryUtils.getToolType(mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack());

                    if (type != -1) {
                        if (toolSlot != toolSlots[type]) {
                            if (moveItems) {
                                this.putToolsInSlot(type, toolSlots);
                            }
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean equipArmor(boolean moveItems) {
        if (this.autoArmor.get()) {
            for (int i = 0; i < this.bestArmorPieces.length; i++) {
                final int piece = this.bestArmorPieces[i];

                if (piece != -1) {
                    int armorPieceSlot = i + 5;
                    final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot).getStack();
                    if (stack != null)
                        continue;

                    if (moveItems)
                        windowClick(piece, 0, 1);

                    return true;
                }
            }
        }

        return false;
    }

    public void windowClick(int slotId, int mouseButtonClicked, int mode) {
        slot = slotId;
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotId, mouseButtonClicked, mode, mc.thePlayer);
        timer.reset();
    }

    private void putItemInSlot(final int slot, final int slotIn) {
        windowClick(slotIn, slot - 36, 2);
    }

    private void putToolsInSlot(final int tool, final int[] toolSlots) {
        final int toolSlot = toolSlots[tool];

        windowClick(this.bestToolSlots[tool],
                toolSlot - 36,
                2);
        this.bestToolSlots[tool] = toolSlot;
    }

    @Override
    public void onEnable() {
        this.clientOpen = mc.currentScreen instanceof GuiInventory;
        this.serverOpen = this.clientOpen;
    }

    @Override
    public void onDisable() {
        this.close();
        this.clear();
    }

    private void open() {
        if (!this.clientOpen && !this.serverOpen) {
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.serverOpen = true;
        }
    }

    private void close() {
        if (!this.clientOpen && this.serverOpen) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
            this.serverOpen = false;
        }
    }

    private void clear() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.gappleStackSlots.clear();
        this.blockSlot.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }
}
