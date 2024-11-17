package net.minecraft.command;

import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class CommandWeather extends CommandBase
{
    public String getCommandName()
    {
        return "weather";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.weather.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1 && args.length <= 2)
        {
            int i = (300 + (new Random()).nextInt(600)) * 20;

            if (args.length >= 2)
            {
                i = parseInt(args[1], 1, 1000000) * 20;
            }

            World world = MinecraftServer.getServer().worldServers[0];
            WorldInfo worldinfo = world.getWorldInfo();

            if ("clear".equalsIgnoreCase(args[0]))
            {
                worldinfo.setCleanWeatherTime(i);
                worldinfo.setRainTime(0);
                worldinfo.setThunderTime(0);
                worldinfo.setRaining(false);
                worldinfo.setThundering(false);
                notifyOperators(sender, this, "commands.weather.clear");
            }
            else if ("rain".equalsIgnoreCase(args[0]))
            {
                worldinfo.setCleanWeatherTime(0);
                worldinfo.setRainTime(i);
                worldinfo.setThunderTime(i);
                worldinfo.setRaining(true);
                worldinfo.setThundering(false);
                notifyOperators(sender, this, "commands.weather.rain");
            }
            else
            {
                if (!"thunder".equalsIgnoreCase(args[0]))
                {
                    throw new WrongUsageException("commands.weather.usage");
                }

                worldinfo.setCleanWeatherTime(0);
                worldinfo.setRainTime(i);
                worldinfo.setThunderTime(i);
                worldinfo.setRaining(true);
                worldinfo.setThundering(true);
                notifyOperators(sender, this, "commands.weather.thunder");
            }
        }
        else
        {
            throw new WrongUsageException("commands.weather.usage");
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "clear", "rain", "thunder"): null;
    }
}
