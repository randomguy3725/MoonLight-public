package net.minecraft.block;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser
{
    private final IBehaviorDispenseItem dropBehavior = new BehaviorDefaultDispenseItem();

    protected IBehaviorDispenseItem getBehavior(ItemStack stack)
    {
        return this.dropBehavior;
    }

    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDropper();
    }

    protected void dispense(World worldIn, BlockPos pos)
    {
        BlockSourceImpl blocksourceimpl = new BlockSourceImpl(worldIn, pos);
        TileEntityDispenser tileentitydispenser = blocksourceimpl.getBlockTileEntity();

        if (tileentitydispenser != null)
        {
            int i = tileentitydispenser.getDispenseSlot();

            if (i < 0)
            {
                worldIn.playAuxSFX(1001, pos, 0);
            }
            else
            {
                ItemStack itemstack = tileentitydispenser.getStackInSlot(i);

                if (itemstack != null)
                {
                    EnumFacing enumfacing = worldIn.getBlockState(pos).getValue(FACING);
                    BlockPos blockpos = pos.offset(enumfacing);
                    IInventory iinventory = TileEntityHopper.getInventoryAtPosition(worldIn, blockpos.getX(), blockpos.getY(), blockpos.getZ());
                    ItemStack itemstack1;

                    if (iinventory == null)
                    {
                        itemstack1 = this.dropBehavior.dispense(blocksourceimpl, itemstack);

                        if (itemstack1 != null && itemstack1.stackSize <= 0)
                        {
                            itemstack1 = null;
                        }
                    }
                    else
                    {
                        itemstack1 = TileEntityHopper.putStackInInventoryAllSlots(iinventory, itemstack.copy().splitStack(1), enumfacing.getOpposite());

                        if (itemstack1 == null)
                        {
                            itemstack1 = itemstack.copy();

                            if (--itemstack1.stackSize <= 0)
                            {
                                itemstack1 = null;
                            }
                        }
                        else
                        {
                            itemstack1 = itemstack.copy();
                        }
                    }

                    tileentitydispenser.setInventorySlotContents(i, itemstack1);
                }
            }
        }
    }
}
