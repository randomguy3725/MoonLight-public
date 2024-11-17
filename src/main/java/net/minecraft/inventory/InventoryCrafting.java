package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCrafting implements IInventory
{
    private final ItemStack[] stackList;
    private final int inventoryWidth;
    private final int inventoryHeight;
    private final Container eventHandler;

    public InventoryCrafting(Container eventHandlerIn, int width, int height)
    {
        int i = width * height;
        this.stackList = new ItemStack[i];
        this.eventHandler = eventHandlerIn;
        this.inventoryWidth = width;
        this.inventoryHeight = height;
    }

    public int getSizeInventory()
    {
        return this.stackList.length;
    }

    public ItemStack getStackInSlot(int index)
    {
        return index >= this.getSizeInventory() ? null : this.stackList[index];
    }

    public ItemStack getStackInRowAndColumn(int row, int column)
    {
        return row >= 0 && row < this.inventoryWidth && column >= 0 && column <= this.inventoryHeight ? this.getStackInSlot(row + column * this.inventoryWidth) : null;
    }

    public String getName()
    {
        return "container.crafting";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public IChatComponent getDisplayName()
    {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
    }

    public ItemStack removeStackFromSlot(int index)
    {
        if (this.stackList[index] != null)
        {
            ItemStack itemstack = this.stackList[index];
            this.stackList[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    public ItemStack decrStackSize(int index, int count)
    {
        if (this.stackList[index] != null)
        {
            if (this.stackList[index].stackSize <= count)
            {
                ItemStack itemstack1 = this.stackList[index];
                this.stackList[index] = null;
                this.eventHandler.onCraftMatrixChanged(this);
                return itemstack1;
            }
            else
            {
                ItemStack itemstack = this.stackList[index].splitStack(count);

                if (this.stackList[index].stackSize == 0)
                {
                    this.stackList[index] = null;
                }

                this.eventHandler.onCraftMatrixChanged(this);
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.stackList[index] = stack;
        this.eventHandler.onCraftMatrixChanged(this);
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
        for (int i = 0; i < this.stackList.length; ++i)
        {
            this.stackList[i] = null;
        }
    }

    public int getHeight()
    {
        return this.inventoryHeight;
    }

    public int getWidth()
    {
        return this.inventoryWidth;
    }
}
