package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandListBans extends CommandBase
{
    public String getCommandName()
    {
        return "banlist";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return (MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isLanServer() || MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer()) && super.canCommandSenderUseCommand(sender);
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.banlist.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1 && args[0].equalsIgnoreCase("ips"))
        {
            sender.addChatMessage(new ChatComponentTranslation("commands.banlist.ips", Integer.valueOf(MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getKeys().length)));
            sender.addChatMessage(new ChatComponentText(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getKeys())));
        }
        else
        {
            sender.addChatMessage(new ChatComponentTranslation("commands.banlist.players", Integer.valueOf(MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getKeys().length)));
            sender.addChatMessage(new ChatComponentText(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getKeys())));
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "players", "ips"): null;
    }
}
