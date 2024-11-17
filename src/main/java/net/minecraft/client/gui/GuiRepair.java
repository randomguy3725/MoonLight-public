package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjglx.input.Keyboard;

public class GuiRepair extends GuiContainer implements ICrafting
{
    private static final ResourceLocation anvilResource = new ResourceLocation("textures/gui/container/anvil.png");
    private final ContainerRepair anvil;
    private GuiTextField nameField;
    private final InventoryPlayer playerInventory;

    public GuiRepair(InventoryPlayer inventoryIn, World worldIn)
    {
        super(new ContainerRepair(inventoryIn, worldIn, Minecraft.getMinecraft().thePlayer));
        this.playerInventory = inventoryIn;
        this.anvil = (ContainerRepair)this.inventorySlots;
    }

    public void initGui()
    {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.nameField = new GuiTextField(0, this.fontRendererObj, i + 62, j + 24, 103, 12);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(30);
        this.inventorySlots.removeCraftingFromCrafters(this);
        this.inventorySlots.onCraftGuiOpened(this);
    }

    public void onGuiClosed()
    {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        this.inventorySlots.removeCraftingFromCrafters(this);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.fontRendererObj.drawString(I18n.format("container.repair"), 60, 6, 4210752);

        if (this.anvil.maximumCost > 0)
        {
            int i = 8453920;
            boolean flag = true;
            String s = I18n.format("container.repair.cost", Integer.valueOf(this.anvil.maximumCost));

            if (this.anvil.maximumCost >= 40 && !this.mc.thePlayer.capabilities.isCreativeMode)
            {
                s = I18n.format("container.repair.expensive");
                i = 16736352;
            }
            else if (!this.anvil.getSlot(2).getHasStack())
            {
                flag = false;
            }
            else if (!this.anvil.getSlot(2).canTakeStack(this.playerInventory.player))
            {
                i = 16736352;
            }

            if (flag)
            {
                int j = -16777216 | (i & 16579836) >> 2 | i & -16777216;
                int k = this.xSize - 8 - this.fontRendererObj.getStringWidth(s);
                int l = 67;

                if (this.fontRendererObj.getUnicodeFlag())
                {
                    drawRect(k - 3, l - 2, this.xSize - 7, l + 10, -16777216);
                    drawRect(k - 2, l - 1, this.xSize - 8, l + 9, -12895429);
                }
                else
                {
                    this.fontRendererObj.drawString(s, k, l + 1, j);
                    this.fontRendererObj.drawString(s, k + 1, l, j);
                    this.fontRendererObj.drawString(s, k + 1, l + 1, j);
                }

                this.fontRendererObj.drawString(s, k, l, i);
            }
        }

        GlStateManager.enableLighting();
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.nameField.textboxKeyTyped(typedChar, keyCode))
        {
            this.renameItem();
        }
        else
        {
            super.keyTyped(typedChar, keyCode);
        }
    }

    private void renameItem()
    {
        String s = this.nameField.getText();
        Slot slot = this.anvil.getSlot(0);

        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName()))
        {
            s = "";
        }

        this.anvil.updateItemName(s);
        this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", (new PacketBuffer(Unpooled.buffer())).writeString(s)));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.nameField.drawTextBox();
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(anvilResource);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(i + 59, j + 20, 0, this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

        if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2).getHasStack())
        {
            this.drawTexturedModalRect(i + 99, j + 45, this.xSize, 0, 28, 21);
        }
    }

    public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList)
    {
        this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
    }

    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
    {
        if (slotInd == 0)
        {
            this.nameField.setText(stack == null ? "" : stack.getDisplayName());
            this.nameField.setEnabled(stack != null);

            if (stack != null)
            {
                this.renameItem();
            }
        }
    }

    public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue)
    {
    }

    public void sendAllWindowProperties(Container p_175173_1_, IInventory p_175173_2_)
    {
    }
}
