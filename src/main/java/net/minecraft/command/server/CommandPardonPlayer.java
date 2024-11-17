package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandPardonPlayer extends CommandBase
{
    public String getCommandName()
    {
        return "pardon";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.unban.usage";
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(sender);
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 1 && args[0].length() > 0)
        {
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            GameProfile gameprofile = minecraftserver.getConfigurationManager().getBannedPlayers().isUsernameBanned(args[0]);

            if (gameprofile == null)
            {
                throw new CommandException("commands.unban.failed", args[0]);
            }
            else
            {
                minecraftserver.getConfigurationManager().getBannedPlayers().removeEntry(gameprofile);
                notifyOperators(sender, this, "commands.unban.success", args[0]);
            }
        }
        else
        {
            throw new WrongUsageException("commands.unban.usage");
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getKeys()) : null;
    }
}
