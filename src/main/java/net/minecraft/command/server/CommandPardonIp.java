package net.minecraft.command.server;

import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandPardonIp extends CommandBase
{
    public String getCommandName()
    {
        return "pardon-ip";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isLanServer() && super.canCommandSenderUseCommand(sender);
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.unbanip.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 1 && args[0].length() > 1)
        {
            Matcher matcher = CommandBanIp.field_147211_a.matcher(args[0]);

            if (matcher.matches())
            {
                MinecraftServer.getServer().getConfigurationManager().getBannedIPs().removeEntry(args[0]);
                notifyOperators(sender, this, "commands.unbanip.success", args[0]);
            }
            else
            {
                throw new SyntaxErrorException("commands.unbanip.invalid");
            }
        }
        else
        {
            throw new WrongUsageException("commands.unbanip.usage");
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getKeys()) : null;
    }
}
