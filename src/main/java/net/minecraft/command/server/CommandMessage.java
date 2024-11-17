package net.minecraft.command.server;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandMessage extends CommandBase
{
    public List<String> getCommandAliases()
    {
        return Arrays.asList("w", "msg");
    }

    public String getCommandName()
    {
        return "tell";
    }

    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.message.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.message.usage");
        }
        else
        {
            EntityPlayer entityplayer = getPlayer(sender, args[0]);

            if (entityplayer == sender)
            {
                throw new PlayerNotFoundException("commands.message.sameTarget");
            }
            else
            {
                IChatComponent ichatcomponent = getChatComponentFromNthArg(sender, args, 1, !(sender instanceof EntityPlayer));
                ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.message.display.incoming", sender.getDisplayName(), ichatcomponent.createCopy());
                ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.message.display.outgoing", entityplayer.getDisplayName(), ichatcomponent.createCopy());
                chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true));
                chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true));
                entityplayer.addChatMessage(chatcomponenttranslation);
                sender.addChatMessage(chatcomponenttranslation1);
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }

    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
