package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCraftResult implements IInventory
{
    private final ItemStack[] stackResult = new ItemStack[1];

    public int getSizeInventory()
    {
        return 1;
    }

    public ItemStack getStackInSlot(int index)
    {
        return this.stackResult[0];
    }

    public String getName()
    {
        return "Result";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public IChatComponent getDisplayName()
    {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
    }

    public ItemStack decrStackSize(int index, int count)
    {
        if (this.stackResult[0] != null)
        {
            ItemStack itemstack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    public ItemStack removeStackFromSlot(int index)
    {
        if (this.stackResult[0] != null)
        {
            ItemStack itemstack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.stackResult[0] = stack;
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public void markDirty()
    {
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
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
        for (int i = 0; i < this.stackResult.length; ++i)
        {
            this.stackResult[i] = null;
        }
    }
}
