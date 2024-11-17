package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements IInventory
{
    private String inventoryTitle;
    private final int slotsCount;
    private final ItemStack[] inventoryContents;
    private List<IInvBasic> changeListeners;
    private boolean hasCustomName;

    public InventoryBasic(String title, boolean customName, int slotCount)
    {
        this.inventoryTitle = title;
        this.hasCustomName = customName;
        this.slotsCount = slotCount;
        this.inventoryContents = new ItemStack[slotCount];
    }

    public InventoryBasic(IChatComponent title, int slotCount)
    {
        this(title.getUnformattedText(), true, slotCount);
    }

    public void addInventoryChangeListener(IInvBasic listener)
    {
        if (this.changeListeners == null)
        {
            this.changeListeners = Lists.newArrayList();
        }

        this.changeListeners.add(listener);
    }

    public void removeInventoryChangeListener(IInvBasic listener)
    {
        this.changeListeners.remove(listener);
    }

    public ItemStack getStackInSlot(int index)
    {
        return index >= 0 && index < this.inventoryContents.length ? this.inventoryContents[index] : null;
    }

    public ItemStack decrStackSize(int index, int count)
    {
        if (this.inventoryContents[index] != null)
        {
            if (this.inventoryContents[index].stackSize <= count)
            {
                ItemStack itemstack1 = this.inventoryContents[index];
                this.inventoryContents[index] = null;
                this.markDirty();
                return itemstack1;
            }
            else
            {
                ItemStack itemstack = this.inventoryContents[index].splitStack(count);

                if (this.inventoryContents[index].stackSize == 0)
                {
                    this.inventoryContents[index] = null;
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

    public ItemStack func_174894_a(ItemStack stack)
    {
        ItemStack itemstack = stack.copy();

        for (int i = 0; i < this.slotsCount; ++i)
        {
            ItemStack itemstack1 = this.getStackInSlot(i);

            if (itemstack1 == null)
            {
                this.setInventorySlotContents(i, itemstack);
                this.markDirty();
                return null;
            }

            if (ItemStack.areItemsEqual(itemstack1, itemstack))
            {
                int j = Math.min(this.getInventoryStackLimit(), itemstack1.getMaxStackSize());
                int k = Math.min(itemstack.stackSize, j - itemstack1.stackSize);

                if (k > 0)
                {
                    itemstack1.stackSize += k;
                    itemstack.stackSize -= k;

                    if (itemstack.stackSize <= 0)
                    {
                        this.markDirty();
                        return null;
                    }
                }
            }
        }

        if (itemstack.stackSize != stack.stackSize)
        {
            this.markDirty();
        }

        return itemstack;
    }

    public ItemStack removeStackFromSlot(int index)
    {
        if (this.inventoryContents[index] != null)
        {
            ItemStack itemstack = this.inventoryContents[index];
            this.inventoryContents[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventoryContents[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    public int getSizeInventory()
    {
        return this.slotsCount;
    }

    public String getName()
    {
        return this.inventoryTitle;
    }

    public boolean hasCustomName()
    {
        return this.hasCustomName;
    }

    public void setCustomName(String inventoryTitleIn)
    {
        this.hasCustomName = true;
        this.inventoryTitle = inventoryTitleIn;
    }

    public IChatComponent getDisplayName()
    {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public void markDirty()
    {
        if (this.changeListeners != null)
        {
            for (int i = 0; i < this.changeListeners.size(); ++i)
            {
                this.changeListeners.get(i).onInventoryChanged(this);
            }
        }
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
        for (int i = 0; i < this.inventoryContents.length; ++i)
        {
            this.inventoryContents[i] = null;
        }
    }
}
