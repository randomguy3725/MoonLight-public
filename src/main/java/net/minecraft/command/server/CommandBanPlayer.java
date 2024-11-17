package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.BlockPos;

public class CommandBanPlayer extends CommandBase
{
    public String getCommandName()
    {
        return "ban";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.ban.usage";
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(sender);
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1 && args[0].length() > 0)
        {
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[0]);

            if (gameprofile == null)
            {
                throw new CommandException("commands.ban.failed", args[0]);
            }
            else
            {
                String s = null;

                if (args.length >= 2)
                {
                    s = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
                }

                UserListBansEntry userlistbansentry = new UserListBansEntry(gameprofile, null, sender.getName(), null, s);
                minecraftserver.getConfigurationManager().getBannedPlayers().addEntry(userlistbansentry);
                EntityPlayerMP entityplayermp = minecraftserver.getConfigurationManager().getPlayerByUsername(args[0]);

                if (entityplayermp != null)
                {
                    entityplayermp.playerNetServerHandler.kickPlayerFromServer("You are banned from this server.");
                }

                notifyOperators(sender, this, "commands.ban.success", args[0]);
            }
        }
        else
        {
            throw new WrongUsageException("commands.ban.usage");
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
