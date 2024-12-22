package wtf.moonlight.features.modules.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjglx.input.Keyboard;
import org.lwjglx.opengl.Display;
import org.lwjglx.util.glu.GLU;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.combat.KillAura;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.modules.impl.world.Scaffold;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.player.InventoryUtils;
import wtf.moonlight.utils.player.MovementCorrection;
import wtf.moonlight.utils.player.RotationUtils;
import wtf.moonlight.utils.render.GLUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@ModuleInfo(name = "Stealer", category = ModuleCategory.Player, key = Keyboard.KEY_L)
public final class Stealer extends Module {
    private final SliderValue delay = new SliderValue("Delay", 1, 0, 5, 1, this);
    public final BoolValue menuCheck = new BoolValue("Menu Check", true, this);
    public final BoolValue silent = new BoolValue("Silent", false, this);
    public final BoolValue silentChestView = new BoolValue("Silent View", true, this);
    public final BoolValue aura = new BoolValue("Aura", false, this);
    private final BoolValue startDelay = new BoolValue("Start Delay", true, this);

    public final BoolValue furnace = new BoolValue("Furnace", false, this);
    public final BoolValue brewingStand = new BoolValue("Brewing Stand", false, this);
    public final BoolValue avoid = new BoolValue("Avoid", false, this, aura::get);
    public final BoolValue display = new BoolValue("Display", true, this);
    private final SliderValue range = new SliderValue("Range", 4f, 1.5f, 4f, this);
    private final TimerUtils timer = new TimerUtils(), timerAura = new TimerUtils(), timerAvoid = new TimerUtils();
    public boolean isStealing;
    private int index;
    private final List<BlockPos> posList = new CopyOnWriteArrayList<>();
    private int prevItem = -1;
    private int chestIndex;
    public static float[] rotation;
    public int slot;
    private BlockPos currentContainerPos;
    private final String[] list = new String[]{"mode", "delivery", "menu", "selector", "game", "gui", "server", "inventory", "play", "teleporter", //
            "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user", "team", "tool", "sure", "trade", "cancel", "accept",  //
            "soul", "book", "recipe", "profile", "tele", "port", "map", "kit", "select", "lobby", "vault", "lock", "anticheat", "travel", "settings", //
            "user", "preference", "compass", "cake", "wars", "buy", "upgrade", "ranged", "potions", "utility"};

    public void rotate(BlockPos blockPos, EnumFacing enumFacing) {
        rotation = RotationUtils.getRotations(blockPos, enumFacing);

        RotationUtils.setRotation(rotation, MovementCorrection.SILENT);
    }

    @Override
    public void onDisable() {
        isStealing = false;
        posList.clear();
        chestIndex = 0;
        super.onDisable();
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        posList.clear();
        chestIndex = 0;
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setTag(String.valueOf(delay.get()));
        rotation = null;
        if (aura.get() && !(isEnabled(Scaffold.class) || getModule(KillAura.class).isBlocking)) {
            if (event.isPre()) {
                if (!isStealing) {
                    for (TileEntity chest : tileEntityList()) {
                        if (!posList.contains(chest.getPos()) && timerAura.hasTimeElapsed(300)) {
                            rotate(chest.getPos(), Block.getFacingDirection(chest.getPos()));
                            if (RotationUtils.rayTrace(RotationUtils.currentRotation, range.get(), 1).getBlockPos().equals(chest.getPos()) && (chest instanceof TileEntityChest || brewingStand.get() && chest instanceof TileEntityBrewingStand || furnace.get() && chest instanceof TileEntityFurnace)) {
                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), chest.getPos(), Block.getFacingDirection(chest.getPos()), getVec3(chest.getPos()));
                                posList.add(chest.getPos());
                                timerAura.reset();
                            }
                        }
                    }
                } else {
                    timerAura.reset();
                }
                if (avoid.get()) {
                    if (!isStealing) {
                        if (!posList.isEmpty()) {
                            if (mc.thePlayer.getDistance(posList.get(chestIndex)) <= range.get()) {
                                if (mc.theWorld.getBlockState(posList.get(chestIndex).add(0, 1, 0)).getBlock() instanceof BlockAir && getBlockSlot() != -1) {
                                    if (timerAvoid.hasTimeElapsed(1000)) {
                                        prevItem = mc.thePlayer.inventory.currentItem;
                                        mc.thePlayer.inventory.currentItem = getBlockSlot();
                                        rotate(posList.get(chestIndex), Block.getFacingDirection(posList.get(chestIndex)));
                                        if (RotationUtils.rayTrace(RotationUtils.currentRotation, range.get(), 1).getBlockPos().equals(posList.get(chestIndex))) {
                                            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), posList.get(chestIndex).add(0, 1, 0), Block.getFacingDirection(posList.get(chestIndex).add(0, 1, 0)), getVec3(posList.get(chestIndex).add(0, 1, 0)))) {
                                                mc.thePlayer.swingItem();
                                            }

                                            chestIndex += 1;
                                            mc.thePlayer.inventory.currentItem = prevItem;
                                            timerAvoid.reset();
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        timerAvoid.reset();
                    }
                }
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (!posList.isEmpty()) {
            posList.forEach(blockPos -> {
                RenderUtils.renderBlock(blockPos, getModule(Interface.class).color(), true, true);
            });
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (silentChestView.get()) {
            if (mc.thePlayer.openContainer == null || mc.currentScreen == null || !isStealing) return;
            Container container = mc.thePlayer.openContainer;
            int slots = container.inventorySlots.size();

            int scaleFactor = event.getScaledResolution().getScaleFactor();

            if (slots > 0) {
                float[] projection = calculate(currentContainerPos, scaleFactor);
                if (projection == null) return;

                float roundX = projection[0] - (164 / 2F);
                float roundY = projection[1] / 1.5F;

                GlStateManager.pushMatrix();
                GlStateManager.translate(roundX + 82, roundY + 30, 0);
                GlStateManager.translate(-(roundX + 82), -(roundY + 30), 0);

                RoundedUtils.drawRound(roundX, roundY, 164, 60, 3, new Color(0, 0, 0, 120));

                double startX = roundX + 5;
                double startY = roundY + 5;

                RenderItem itemRender = mc.getRenderItem();

                GlStateManager.pushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                itemRender.zLevel = 200.0F;

                for (Slot slot : container.inventorySlots) {
                    if (!slot.inventory.equals(mc.thePlayer.inventory)) {
                        int x = (int) (startX + (slot.slotNumber % 9) * 18);
                        int y = (int) (startY + ((double) slot.slotNumber / 9) * 18);

                        itemRender.renderItemAndEffectIntoGUI(slot.getStack(), x, y);
                    }
                }

                GlStateManager.popMatrix();

                itemRender.zLevel = 0.0F;
                GlStateManager.popMatrix();
                GlStateManager.disableLighting();
            }
        }
    }

    public float[] calculate(BlockPos blockPos, int factor) {
        try {
            float renderX = (float) mc.getRenderManager().renderPosX;
            float renderY = (float) mc.getRenderManager().renderPosY;
            float renderZ = (float) mc.getRenderManager().renderPosZ;

            float x = blockPos.getX() + 0.5f - renderX;
            float y = blockPos.getY() + 0.5f - renderY;
            float z = blockPos.getZ() + 0.5f - renderZ;

            float[] projectedCenter = project(x, y, z, factor);
            if (projectedCenter != null && projectedCenter[2] >= 0.0D && projectedCenter[2] < 1.0D) {
                return new float[]{projectedCenter[0],projectedCenter[1],projectedCenter[0],projectedCenter[1]};
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static float[] project(double x, double y, double z, int factor) {
        if (GLU.gluProject((float) x, (float) y, (float) z, ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, ActiveRenderInfo.OBJECTCOORDS)) {
            return new float[]{(ActiveRenderInfo.OBJECTCOORDS.get(0) / factor), ((Display.getHeight() - ActiveRenderInfo.OBJECTCOORDS.get(1)) / factor), ActiveRenderInfo.OBJECTCOORDS.get(2)};
        }
        return null;
    }

    private List<TileEntity> tileEntityList() {
        return mc.theWorld.loadedTileEntityList.stream().filter(te -> mc.thePlayer.getDistance(te.getPos()) <= range.get())
                .sorted(Comparator.comparing(o -> mc.thePlayer.getDistance(((TileEntity) o).getPos())).reversed()).collect(Collectors.toList());
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
            if (mc.thePlayer.openContainer != null) {
                if (mc.thePlayer.openContainer instanceof ContainerChest) {
                    if (isStealing) {
                        ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;
                        if (menuCheck.get()) {

                            String name = container.getLowerChestInventory().getDisplayName().getUnformattedText().toLowerCase();
                            for (String str : list) {
                                if (name.contains(str))
                                    return;
                            }
                        }

                        for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
                            if (container.getLowerChestInventory().getStackInSlot(i) != null && (timer.hasTimeElapsed((long) (delay.get() * 100L)) || delay.get() == 0) && InventoryUtils.isValid(container.getLowerChestInventory().getStackInSlot(i))) {
                                slot = i;
                                mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                                timer.reset();
                            }
                        }
                        if (InventoryUtils.isInventoryFull() || InventoryUtils.isInventoryEmpty(container.getLowerChestInventory())) {
                            mc.thePlayer.closeScreen();
                            isStealing = false;
                        }
                    }
                }

                if (furnace.get()) {
                    if (mc.thePlayer.openContainer instanceof ContainerFurnace container) {
                        if (isStealing) {
                            for (index = 0; index < container.tileFurnace.getSizeInventory(); ++index) {
                                if (container.tileFurnace.getStackInSlot(index) != null || (timer.hasTimeElapsed((long) (delay.get() * 100L)) || delay.get() == 0)) {
                                    mc.playerController.windowClick(container.windowId, index, 0, 1, mc.thePlayer);
                                    timer.reset();
                                }
                            }

                            if (isFurnaceEmpty(container)) {
                                mc.thePlayer.closeScreen();
                                isStealing = false;
                            }
                        }
                    }
                }

                if (brewingStand.get()) {
                    if (mc.thePlayer.openContainer instanceof ContainerBrewingStand container) {
                        if (isStealing) {
                            for (index = 0; index < container.tileBrewingStand.getSizeInventory(); ++index) {
                                if (container.tileBrewingStand.getStackInSlot(index) != null || (timer.hasTimeElapsed((long) (delay.get() * 100L)) || delay.get() == 0)) {
                                    mc.playerController.windowClick(container.windowId, index, 0, 1, mc.thePlayer);
                                    timer.reset();
                                }
                            }

                            if (isBrewingStandEmpty(container)) {
                                mc.thePlayer.closeScreen();
                                isStealing = false;
                            }
                        }
                    }
                }
            }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S2DPacketOpenWindow packetOpenWindow) {
            for (String blacklisted : list) {
                if (packetOpenWindow.getWindowTitle().getUnformattedText().toLowerCase().contains(blacklisted)) {
                    isStealing = false;
                    return;
                }
            }
            if(startDelay.get())
                timer.reset();
            isStealing = packetOpenWindow.getGuiId().equals("minecraft:chest") || furnace.get() && packetOpenWindow.getGuiId().equals("minecraft:furnace")
            //bug
            //|| brewingStand.get() && packetOpenWindow.getGuiId().equals("minecraft:brewing_stand")
            ;
        }

        if (silentChestView.get()) {
            Packet<?> packet = event.getPacket();
            if (packet instanceof C08PacketPlayerBlockPlacement wrapper) {
                if (wrapper.getPosition() != null) {
                    Block block = mc.theWorld.getBlockState(wrapper.getPosition()).getBlock();
                    if (block instanceof BlockContainer) {
                        currentContainerPos = wrapper.getPosition();
                    }
                }
            }
        }
    }

    private boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < c.tileFurnace.getSizeInventory(); ++i) {
            if (c.tileFurnace.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    private boolean isBrewingStandEmpty(ContainerBrewingStand c) {
        for (int i = 0; i < c.tileBrewingStand.getSizeInventory(); ++i) {
            if (c.tileBrewingStand.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    public static int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack()
                    && mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock) {
                return i;
            }
        }
        return -1;
    }

    public Vec3 getVec3(BlockPos pos) {
        Vec3 vector = new Vec3(pos);
        EnumFacing facing = Block.getFacingDirection(pos);
        double random = ThreadLocalRandom.current().nextDouble();

        if (facing == EnumFacing.NORTH) {
            vector.xCoord += random;
        } else if (facing == EnumFacing.SOUTH) {
            vector.xCoord += random;
            vector.zCoord += 1.0;
        } else if (facing == EnumFacing.WEST) {
            vector.zCoord += random;
        } else if (facing == EnumFacing.EAST) {
            vector.zCoord += random;
            vector.xCoord += 1.0;
        }

        if (facing == EnumFacing.UP) {
            vector.xCoord += random;
            vector.zCoord += random;
            vector.yCoord += 1.0;
        } else {
            vector.yCoord += random;
        }

        return vector;
    }
}
