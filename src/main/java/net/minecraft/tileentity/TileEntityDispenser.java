package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntityLockable implements IInventory
{
    private static final Random RNG = new Random();
    private ItemStack[] stacks = new ItemStack[9];
    protected String customName;

    public int getSizeInventory()
    {
        return 9;
    }

    public ItemStack getStackInSlot(int index)
    {
        return this.stacks[index];
    }

    public ItemStack decrStackSize(int index, int count)
    {
        if (this.stacks[index] != null)
        {
            if (this.stacks[index].stackSize <= count)
            {
                ItemStack itemstack1 = this.stacks[index];
                this.stacks[index] = null;
                this.markDirty();
                return itemstack1;
            }
            else
            {
                ItemStack itemstack = this.stacks[index].splitStack(count);

                if (this.stacks[index].stackSize == 0)
                {
                    this.stacks[index] = null;
                }

                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    public ItemStack removeStackFromSlot(int index)
    {
        if (this.stacks[index] != null)
        {
            ItemStack itemstack = this.stacks[index];
            this.stacks[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    public int getDispenseSlot()
    {
        int i = -1;
        int j = 1;

        for (int k = 0; k < this.stacks.length; ++k)
        {
            if (this.stacks[k] != null && RNG.nextInt(j++) == 0)
            {
                i = k;
            }
        }

        return i;
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.stacks[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    public int addItemStack(ItemStack stack)
    {
        for (int i = 0; i < this.stacks.length; ++i)
        {
            if (this.stacks[i] == null || this.stacks[i].getItem() == null)
            {
                this.setInventorySlotContents(i, stack);
                return i;
            }
        }

        return -1;
    }

    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.dispenser";
    }

    public void setCustomName(String customName)
    {
        this.customName = customName;
    }

    public boolean hasCustomName()
    {
        return this.customName != null;
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.stacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < this.stacks.length)
            {
                this.stacks[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.stacks.length; ++i)
        {
            if (this.stacks[i] != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.stacks[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        compound.setTag("Items", nbttaglist);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    public String getGuiID()
    {
        return "minecraft:dispenser";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerDispenser(playerInventory, this);
    }

    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value)
    {
    }

    public int getFieldCount()
    {
        return 0;
    }

    public void clear()
    {
        for (int i = 0; i < this.stacks.length; ++i)
        {
            this.stacks[i] = null;
        }
    }
}
