package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandDeOp extends CommandBase
{
    public String getCommandName()
    {
        return "deop";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.deop.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 1 && args[0].length() > 0)
        {
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            GameProfile gameprofile = minecraftserver.getConfigurationManager().getOppedPlayers().getGameProfileFromName(args[0]);

            if (gameprofile == null)
            {
                throw new CommandException("commands.deop.failed", args[0]);
            }
            else
            {
                minecraftserver.getConfigurationManager().removeOp(gameprofile);
                notifyOperators(sender, this, "commands.deop.success", args[0]);
            }
        }
        else
        {
            throw new WrongUsageException("commands.deop.usage");
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getOppedPlayerNames()) : null;
    }
}
