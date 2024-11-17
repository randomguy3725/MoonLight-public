package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandOp extends CommandBase
{
    public String getCommandName()
    {
        return "op";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.op.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 1 && args[0].length() > 0)
        {
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[0]);

            if (gameprofile == null)
            {
                throw new CommandException("commands.op.failed", args[0]);
            }
            else
            {
                minecraftserver.getConfigurationManager().addOp(gameprofile);
                notifyOperators(sender, this, "commands.op.success", args[0]);
            }
        }
        else
        {
            throw new WrongUsageException("commands.op.usage");
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            String s = args[args.length - 1];
            List<String> list = Lists.newArrayList();

            for (GameProfile gameprofile : MinecraftServer.getServer().getGameProfiles())
            {
                if (!MinecraftServer.getServer().getConfigurationManager().canSendCommands(gameprofile) && doesStringStartWith(s, gameprofile.getName()))
                {
                    list.add(gameprofile.getName());
                }
            }

            return list;
        }
        else
        {
            return null;
        }
    }
}
