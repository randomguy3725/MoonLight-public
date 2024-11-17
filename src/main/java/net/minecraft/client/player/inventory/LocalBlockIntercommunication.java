package net.minecraft.client.player.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;

public class LocalBlockIntercommunication implements IInteractionObject
{
    private final String guiID;
    private final IChatComponent displayName;

    public LocalBlockIntercommunication(String guiIdIn, IChatComponent displayNameIn)
    {
        this.guiID = guiIdIn;
        this.displayName = displayNameIn;
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        throw new UnsupportedOperationException();
    }

    public String getName()
    {
        return this.displayName.getUnformattedText();
    }

    public boolean hasCustomName()
    {
        return true;
    }

    public String getGuiID()
    {
        return this.guiID;
    }

    public IChatComponent getDisplayName()
    {
        return this.displayName;
    }
}
