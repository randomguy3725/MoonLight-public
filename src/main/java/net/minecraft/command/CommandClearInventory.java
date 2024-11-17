package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandClearInventory extends CommandBase
{
    public String getCommandName()
    {
        return "clear";
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.clear.usage";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP entityplayermp = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(sender, args[0]);
        Item item = args.length >= 2 ? getItemByText(sender, args[1]) : null;
        int i = args.length >= 3 ? parseInt(args[2], -1) : -1;
        int j = args.length >= 4 ? parseInt(args[3], -1) : -1;
        NBTTagCompound nbttagcompound = null;

        if (args.length >= 5)
        {
            try
            {
                nbttagcompound = JsonToNBT.getTagFromJson(buildString(args, 4));
            }
            catch (NBTException nbtexception)
            {
                throw new CommandException("commands.clear.tagError", nbtexception.getMessage());
            }
        }

        if (args.length >= 2 && item == null)
        {
            throw new CommandException("commands.clear.failure", entityplayermp.getName());
        }
        else
        {
            int k = entityplayermp.inventory.clearMatchingItems(item, i, j, nbttagcompound);
            entityplayermp.inventoryContainer.detectAndSendChanges();

            if (!entityplayermp.capabilities.isCreativeMode)
            {
                entityplayermp.updateHeldItem();
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, k);

            if (k == 0)
            {
                throw new CommandException("commands.clear.failure", entityplayermp.getName());
            }
            else
            {
                if (j == 0)
                {
                    sender.addChatMessage(new ChatComponentTranslation("commands.clear.testing", entityplayermp.getName(), Integer.valueOf(k)));
                }
                else
                {
                    notifyOperators(sender, this, "commands.clear.success", entityplayermp.getName(), Integer.valueOf(k));
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.func_147209_d()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
    }

    protected String[] func_147209_d()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
