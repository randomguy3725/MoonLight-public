package net.minecraft.command;

public interface IAdminCommand
{
    void notifyOperators(ICommandSender sender, ICommand command, int flags, String msgFormat, Object... msgParams);
}
