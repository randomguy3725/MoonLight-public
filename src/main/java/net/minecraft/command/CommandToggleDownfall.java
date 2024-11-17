package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandToggleDownfall extends CommandBase
{
    public String getCommandName()
    {
        return "toggledownfall";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.downfall.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        this.toggleDownfall();
        notifyOperators(sender, this, "commands.downfall.success");
    }

    protected void toggleDownfall()
    {
        WorldInfo worldinfo = MinecraftServer.getServer().worldServers[0].getWorldInfo();
        worldinfo.setRaining(!worldinfo.isRaining());
    }
}
