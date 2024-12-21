package wtf.moonlight.features.modules.impl.world;

import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

@ModuleInfo(name = "Scaffold2", category = ModuleCategory.World)
public class Scaffold2 extends Module {
    private PlaceData lastPlace;
    private int oloSlot = -1;
    private BlockPos previousBlock;

    @Override
    public void onEnable() {
        oloSlot = mc.thePlayer.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        mc.thePlayer.inventory.currentItem = oloSlot;
    }

    @EventTarget
    public void onMotion(MotionEvent event){
        if (getSlot() == -1) return;
        mc.thePlayer.inventory.currentItem = getSlot();
    }

    @EventTarget
    public void onUpdate(MotionEvent event){
        place();
        place();
        place();
    }

    private int getSlot() {
        int slot = -1;
        int highestStack = -1;
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        for (int i = 0; i < 9; ++i) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && canPlaceBlock((ItemBlock) itemStack.getItem()) && itemStack.stackSize > 0) {
                if (itemStack.stackSize > highestStack) {
                    highestStack = itemStack.stackSize;
                    slot = i;
                }
            }
        }
        return slot;
    }

    private boolean canPlaceBlock(ItemBlock ib){
        Block block = ib.getBlock();
        if (block == null){
            return false;
        }
        return !isInteractable(block) && !(block instanceof BlockTNT) && !(block instanceof BlockSlab) && !(block instanceof BlockWeb) && !(block instanceof BlockLever) && !(block instanceof BlockButton) && !(block instanceof BlockSkull) && !(block instanceof BlockLiquid) && !(block instanceof BlockCactus) && !(block instanceof BlockCarpet) && !(block instanceof BlockTripWire) && !(block instanceof BlockTripWireHook) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFlower) && !(block instanceof BlockFlowerPot) && !(block instanceof BlockSign) && !(block instanceof BlockLadder) && !(block instanceof BlockTorch) && !(block instanceof BlockRedstoneTorch) && !(block instanceof BlockFence) && !(block instanceof BlockPane) && !(block instanceof BlockStainedGlassPane) && !(block instanceof BlockGravel) && !(block instanceof BlockClay) && !(block instanceof BlockSand) && !(block instanceof BlockSoulSand);
    }

    private static boolean isInteractable(Block block) {
        return block instanceof BlockFurnace || block instanceof BlockFenceGate || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockEnchantmentTable || block instanceof BlockBrewingStand || block instanceof BlockBed || block instanceof BlockDropper || block instanceof BlockDispenser || block instanceof BlockHopper || block instanceof BlockAnvil || block == Blocks.crafting_table;
    }

    private void place(){
        lastPlace = findBlock();
        if (lastPlace == null) return;
        mc.thePlayer.rayTrace(200 , 10);
        if (mc.playerController.onPlayerRightClick(mc.thePlayer , mc.theWorld , mc.thePlayer.getHeldItem() , lastPlace.blockPos , lastPlace.facing , getVec3(lastPlace))){
            mc.thePlayer.swingItem();
        }

        previousBlock = findBlock().blockPos.offset(findBlock().getEnumFacing());
    }

    private Vec3 getVec3(PlaceData data) {
        BlockPos pos = data.blockPos;
        EnumFacing face = data.facing;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        x += face.getFrontOffsetX() / 2.0D;
        z += face.getFrontOffsetZ() / 2.0D;
        y += face.getFrontOffsetY() / 2.0D;

        return new Vec3(x, y, z);
    }

    private PlaceData findBlock(){
        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
        EnumFacing[] facings = { EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP };
        BlockPos[] offsets = { new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0) };

        if (previousBlock != null && previousBlock.getY() > mc.thePlayer.posY) {
            previousBlock = null;
        }
        for (int lastCheck = 0; lastCheck < 2; lastCheck++) {
            for (int i = 0; i < offsets.length; i++) {
                BlockPos newPos = pos.add(offsets[i]);
                Block block = mc.theWorld.getBlockState(newPos).getBlock();
                if (newPos.equals(previousBlock)) {
                    return new PlaceData(facings[i], newPos);
                }
                if (lastCheck == 0) {
                    continue;
                }
                if (!block.getMaterial().isReplaceable() && !isInteractable(block)) {
                    return new PlaceData(facings[i], newPos);
                }
            }
        }

        BlockPos[] additionalOffsets = { // adjust these for perfect placement
                pos.add(-1, 0, 0),
                pos.add(1, 0, 0),
                pos.add(0, 0, 1),
                pos.add(0, 0, -1),
                pos.add(0, -1, 0),
        };
        for (int lastCheck = 0; lastCheck < 2; lastCheck++) {
            for (BlockPos additionalPos : additionalOffsets) {
                for (int i = 0; i < offsets.length; i++) {
                    BlockPos newPos = additionalPos.add(offsets[i]);
                    Block block = mc.theWorld.getBlockState(newPos).getBlock();
                    if (newPos.equals(previousBlock)) {
                        return new PlaceData(facings[i], newPos);
                    }
                    if (lastCheck == 0) {
                        continue;
                    }
                    if (!block.getMaterial().isReplaceable() && !isInteractable(block)) {
                        return new PlaceData(facings[i], newPos);
                    }
                }
            }
        }
        BlockPos[] additionalOffsets2 = { // adjust these for perfect placement
                new BlockPos(-1, 0, 0),
                new BlockPos(1, 0, 0),
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, -1),
                new BlockPos(0, -1, 0),
        };
        for (int lastCheck = 0; lastCheck < 2; lastCheck++) {
            for (BlockPos additionalPos2 : additionalOffsets2) {
                for (BlockPos additionalPos : additionalOffsets) {
                    for (int i = 0; i < offsets.length; i++) {
                        BlockPos newPos = additionalPos2.add(additionalPos.add(offsets[i]));
                        Block block = mc.theWorld.getBlockState(newPos).getBlock();
                        if (newPos.equals(previousBlock)) {
                            return new PlaceData(facings[i], newPos);
                        }
                        if (lastCheck == 0) {
                            continue;
                        }
                        if (!block.getMaterial().isReplaceable() && !isInteractable(block)) {
                            return new PlaceData(facings[i], newPos);
                        }
                    }
                }
            }
        }
        return null;
    }

    static class PlaceData {
        EnumFacing facing;
        BlockPos blockPos;

        PlaceData(EnumFacing enumFacing, BlockPos blockPos) {
            this.facing = enumFacing;
            this.blockPos = blockPos;
        }

        EnumFacing getEnumFacing() {
            return facing;
        }
    }
}
