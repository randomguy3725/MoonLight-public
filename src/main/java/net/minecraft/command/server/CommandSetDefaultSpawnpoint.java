package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandSetDefaultSpawnpoint extends CommandBase
{
    public String getCommandName()
    {
        return "setworldspawn";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.setworldspawn.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        BlockPos blockpos;

        if (args.length == 0)
        {
            blockpos = getCommandSenderAsPlayer(sender).getPosition();
        }
        else
        {
            if (args.length != 3 || sender.getEntityWorld() == null)
            {
                throw new WrongUsageException("commands.setworldspawn.usage");
            }

            blockpos = parseBlockPos(sender, args, 0, true);
        }

        sender.getEntityWorld().setSpawnPoint(blockpos);
        MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new S05PacketSpawnPosition(blockpos));
        notifyOperators(sender, this, "commands.setworldspawn.success", Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ()));
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : null;
    }
}
