package wtf.moonlight.features.modules.impl.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.TeleportEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.ContinualAnimation;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.player.RotationUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.text.DecimalFormat;

@ModuleInfo(name = "BedNuker", category = ModuleCategory.World)
public class BedNuker extends Module {
    public final SliderValue breakRange = new SliderValue("Break Range", 4, 1, 5, 1, this);
    public final BoolValue breakSurroundings = new BoolValue("Break Top", true, this);
    public final BoolValue rotOnPacket = new BoolValue("Rot On Packet", true, this);
    public final BoolValue autoTool = new BoolValue("Auto Tool", true, this);
    public final BoolValue autoToolOnPacket = new BoolValue("Auto Tool On Packet", true, this);
    public final BoolValue progressText = new BoolValue("Progress Text", true, this);
    public final BoolValue progressBar = new BoolValue("Progress Bar", false, this);
    public final BoolValue whitelistOwnBed = new BoolValue("Whitelist Own Bed", true, this);
    public BlockPos bedPos;
    public boolean rotate = false;
    private int breakTicks;
    private int delayTicks;
    private Vec3 home;
    public ContinualAnimation barAnim = new ContinualAnimation();
    public ContinualAnimation textAnim = new ContinualAnimation();

    @Override
    public void onEnable() {
        rotate = false;
        bedPos = null;

        breakTicks = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        reset(true);
        super.onDisable();
    }

    @EventTarget
    public void onTeleport(TeleportEvent event){
        if(whitelistOwnBed.get()){
            final double distance = mc.thePlayer.getDistance(event.getPosX(), event.getPosY(), event.getPosZ());

            if (distance > 40) {
                home = new Vec3(event.getPosX(), event.getPosY(), event.getPosZ());
            }
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event){
        if (Moonlight.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled() && Moonlight.INSTANCE.getModuleManager().getModule(Scaffold.class).data == null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
            reset(true);
            return;
        }

        getBedPos();

        if (bedPos != null) {
            if (rotate) {
                float[] rot = RotationUtils.getRotationToBlock(bedPos, getEnumFacing(bedPos));
                RotationUtils.setRotation(rot);
                rotate = false;
            }
            mine(bedPos);
        } else {
            reset(true);
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (progressText.get() && bedPos != null) {
            RenderUtils.renderBlock(bedPos, getModule(Interface.class).color(), true, true);

            if (breakTicks == 0.0f)
                return;

            final double n = bedPos.getX() + 0.5 - mc.getRenderManager().viewerPosX;
            final double n2 = bedPos.getY() + 0.5 - mc.getRenderManager().viewerPosY;
            final double n3 = bedPos.getZ() + 0.5 - mc.getRenderManager().viewerPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) n, (float) n2, (float) n3);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-0.02266667f, -0.02266667f, -0.02266667f);
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            String progressStr = (int) (100.0 * (Math.min(1.0, (double) breakTicks / 10))) + "%";
            mc.fontRendererObj.drawString(progressStr, (float) (-mc.fontRendererObj.getStringWidth(progressStr) / 2), -3.0f, -1, true);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (progressBar.get() && bedPos != null) {

            if (breakTicks == 0.0f)
                return;

            final ScaledResolution resolution = event.getScaledResolution();
            final int x = resolution.getScaledWidth() / 2;
            final int y = resolution.getScaledHeight() - 70;
            final float thickness = 6;

            float percentage = Math.min(breakTicks, 10f) / 10f;

            final int width = resolution.getScaledWidth() / 4;
            final int half = width / 2;
            barAnim.animate(width * percentage, 40);
            textAnim.animate(percentage * 100f,10);

            RoundedUtils.drawRound(x - half, y, width, thickness, thickness / 2, new Color(getModule(Interface.class).bgColor(),true));

            RoundedUtils.drawGradientHorizontal(x - half, y, barAnim.getOutput(), thickness, thickness / 2, new Color(getModule(Interface.class).color(0)), new Color(getModule(Interface.class).color(90)));

            Fonts.interRegular.get(12).drawCenteredStringWithShadow(new DecimalFormat("0.0").format(textAnim.getOutput()) + "%", x, y + 1, -1);
        }
    }


    private void getBedPos() {
        if (home != null && mc.thePlayer.getDistanceSq(home.xCoord, home.yCoord, home.zCoord) < 35 * 35 && whitelistOwnBed.get()) {
            return;
        }
        bedPos = null;
        double range = breakRange.get();
        for (double x = mc.thePlayer.posX - range; x <= mc.thePlayer.posX + range; x++) {
            for (double y = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - range; y <= mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + range; y++) {
                for (double z = mc.thePlayer.posZ - range; z <= mc.thePlayer.posZ + range; z++) {
                    BlockPos pos = new BlockPos((int) x, (int) y, (int) z);

                    if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockBed && mc.theWorld.getBlockState(pos).getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
                        if (breakSurroundings.get() && isBedCovered(pos)) {
                            bedPos = pos.add(0, 1, 0);
                        } else {
                            bedPos = pos;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void mine(BlockPos blockPos) {
        if (delayTicks > 0) {
            delayTicks--;
            return;
        }

        IBlockState blockState = mc.theWorld.getBlockState(blockPos);

        if (blockState.getBlock() instanceof BlockAir) {
            return;
        }

        float totalBreakTicks = getBreakTicks(bedPos, autoTool.get() && autoToolOnPacket.get() && PlayerUtils.findTool(bedPos) != -1 ? PlayerUtils.findTool(bedPos) : mc.thePlayer.inventory.currentItem);
        if (breakTicks == 0) {
            rotate = true;
            if (autoTool.get() && autoToolOnPacket.get()) {
                doAutoTool(blockPos);
            }
            mc.thePlayer.swingItem();
            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bedPos, EnumFacing.UP));
        } else if (breakTicks >= totalBreakTicks) {
            rotate = true;
            if (autoTool.get() && autoToolOnPacket.get()) {
                doAutoTool(blockPos);
            }
            mc.thePlayer.swingItem();
            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, bedPos, EnumFacing.UP));

            //test
            mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(),blockPos, 1);

            reset(false);
            return;
        } else {
            if (!rotOnPacket.get()) {
                rotate = true;
            }

            if (autoTool.get()) {
                if (!autoToolOnPacket.get()) {
                    doAutoTool(blockPos);
                } else {
                    mc.thePlayer.inventory.currentItem = 0;
                }
            }

            mc.thePlayer.swingItem();
        }

        breakTicks += 1;

        int currentProgress = (int) (((double) breakTicks / totalBreakTicks) * 100);
        mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), bedPos, currentProgress / 10);
    }

    private void reset(boolean resetRotate) {
        if (bedPos != null) {
            mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), bedPos, -1);
            //test
            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,bedPos,EnumFacing.DOWN));
        }

        breakTicks = 0;
        delayTicks = 5;
        bedPos = null;
        rotate = !resetRotate;
    }
    private void doAutoTool(BlockPos pos) {
        if(PlayerUtils.findTool(pos) != -1)
            mc.thePlayer.inventory.currentItem = PlayerUtils.findTool(pos);
    }

    public static boolean isBed(BlockPos blockPos) {
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        return block instanceof BlockBed;
    }

    private boolean isBedCovered(BlockPos headBlockBedPos) {
        BlockPos headBlockBedPosOffSet1 = headBlockBedPos.add(1, 0, 0);
        BlockPos headBlockBedPosOffSet2 = headBlockBedPos.add(-1, 0, 0);
        BlockPos headBlockBedPosOffSet3 = headBlockBedPos.add(0, 0, 1);
        BlockPos headBlockBedPosOffSet4 = headBlockBedPos.add(0, 0, -1);

        if (!isBlockCovered(headBlockBedPos)) {
            return false;
        } else if (mc.theWorld.getBlockState(headBlockBedPosOffSet1).getBlock() instanceof BlockBed && mc.theWorld.getBlockState(headBlockBedPosOffSet1).getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            return isBlockCovered(headBlockBedPosOffSet1);
        } else if (mc.theWorld.getBlockState(headBlockBedPosOffSet2).getBlock() instanceof BlockBed && mc.theWorld.getBlockState(headBlockBedPosOffSet2).getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            return isBlockCovered(headBlockBedPosOffSet2);
        } else if (mc.theWorld.getBlockState(headBlockBedPosOffSet3).getBlock() instanceof BlockBed && mc.theWorld.getBlockState(headBlockBedPosOffSet3).getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            return isBlockCovered(headBlockBedPosOffSet3);
        } else if (mc.theWorld.getBlockState(headBlockBedPosOffSet4).getBlock() instanceof BlockBed && mc.theWorld.getBlockState(headBlockBedPosOffSet4).getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT) {
            return isBlockCovered(headBlockBedPosOffSet4);
        }

        return false;
    }

    private boolean isBlockCovered(BlockPos blockPos) {
        BlockPos[] directions = {
                blockPos.add(0, 1, 0), // Up
                blockPos.add(1, 0, 0), // East
                blockPos.add(-1, 0, 0), // West
                blockPos.add(0, 0, 1), // South
                blockPos.add(0, 0, -1) // North
        };

        for (BlockPos pos : directions) {
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            if (block instanceof BlockAir || block.getMaterial() instanceof MaterialLiquid) {
                return false;
            }
        }

        return true;
    }

    public static EnumFacing getEnumFacing(BlockPos pos) {
        Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        if (pos.getY() > eyesPos.yCoord) {
            if (PlayerUtils.isReplaceable(pos.add(0, -1, 0))) {
                return EnumFacing.DOWN;
            } else {
                return mc.thePlayer.getHorizontalFacing().getOpposite();
            }
        }

        if (!PlayerUtils.isReplaceable(pos.add(0, 1, 0))) {
            return mc.thePlayer.getHorizontalFacing().getOpposite();
        }

        return EnumFacing.UP;
    }

    private float getBreakTicks(BlockPos bp, int tool) {
        int oldHeld = mc.thePlayer.inventory.currentItem;

        mc.thePlayer.inventory.currentItem = tool;
        IBlockState bs = mc.theWorld.getBlockState(bp);
        float ticks = 1f / bs.getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, bp);

        mc.thePlayer.inventory.currentItem = oldHeld;
        return ticks;
    }
}
