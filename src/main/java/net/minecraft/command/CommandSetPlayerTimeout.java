package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandSetPlayerTimeout extends CommandBase
{
    public String getCommandName()
    {
        return "setidletimeout";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.setidletimeout.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1)
        {
            throw new WrongUsageException("commands.setidletimeout.usage");
        }
        else
        {
            int i = parseInt(args[0], 0);
            MinecraftServer.getServer().setPlayerIdleTimeout(i);
            notifyOperators(sender, this, "commands.setidletimeout.success", Integer.valueOf(i));
        }
    }
}
