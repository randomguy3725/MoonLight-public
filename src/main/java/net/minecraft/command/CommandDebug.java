package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase
{
    private static final Logger logger = LogManager.getLogger();
    private long profileStartTime;
    private int profileStartTick;

    public String getCommandName()
    {
        return "debug";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.debug.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.debug.usage");
        }
        else
        {
            if (args[0].equals("start"))
            {
                if (args.length != 1)
                {
                    throw new WrongUsageException("commands.debug.usage");
                }

                notifyOperators(sender, this, "commands.debug.start");
                MinecraftServer.getServer().enableProfiling();
                this.profileStartTime = MinecraftServer.getCurrentTimeMillis();
                this.profileStartTick = MinecraftServer.getServer().getTickCounter();
            }
            else
            {
                if (!args[0].equals("stop"))
                {
                    throw new WrongUsageException("commands.debug.usage");
                }

                if (args.length != 1)
                {
                    throw new WrongUsageException("commands.debug.usage");
                }

                if (!MinecraftServer.getServer().theProfiler.profilingEnabled)
                {
                    throw new CommandException("commands.debug.notStarted");
                }

                long i = MinecraftServer.getCurrentTimeMillis();
                int j = MinecraftServer.getServer().getTickCounter();
                long k = i - this.profileStartTime;
                int l = j - this.profileStartTick;
                this.saveProfileResults(k, l);
                MinecraftServer.getServer().theProfiler.profilingEnabled = false;
                notifyOperators(sender, this, "commands.debug.stop", Float.valueOf((float)k / 1000.0F), Integer.valueOf(l));
            }
        }
    }

    private void saveProfileResults(long timeSpan, int tickSpan)
    {
        File file1 = new File(MinecraftServer.getServer().getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
        file1.getParentFile().mkdirs();

        try
        {
            FileWriter filewriter = new FileWriter(file1);
            filewriter.write(this.getProfileResults(timeSpan, tickSpan));
            filewriter.close();
        }
        catch (Throwable throwable)
        {
            logger.error("Could not save profiler results to " + file1, throwable);
        }
    }

    private String getProfileResults(long timeSpan, int tickSpan)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Profiler Results ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(getWittyComment());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time span: ").append(timeSpan).append(" ms\n");
        stringbuilder.append("Tick span: ").append(tickSpan).append(" ticks\n");
        stringbuilder.append("// This is approximately ").append(String.format("%.2f", Float.valueOf((float)tickSpan / ((float)timeSpan / 1000.0F)))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.func_147202_a(0, "root", stringbuilder);
        stringbuilder.append("--- END PROFILE DUMP ---\n\n");
        return stringbuilder.toString();
    }

    private void func_147202_a(int p_147202_1_, String p_147202_2_, StringBuilder stringBuilder)
    {
        List<Profiler.Result> list = MinecraftServer.getServer().theProfiler.getProfilingData(p_147202_2_);

        if (list != null && list.size() >= 3)
        {
            for (int i = 1; i < list.size(); ++i)
            {
                Profiler.Result profiler$result = list.get(i);
                stringBuilder.append(String.format("[%02d] ", Integer.valueOf(p_147202_1_)));

                for (int j = 0; j < p_147202_1_; ++j)
                {
                    stringBuilder.append(" ");
                }

                stringBuilder.append(profiler$result.field_76331_c).append(" - ").append(String.format("%.2f", Double.valueOf(profiler$result.field_76332_a))).append("%/").append(String.format("%.2f", Double.valueOf(profiler$result.field_76330_b))).append("%\n");

                if (!profiler$result.field_76331_c.equals("unspecified"))
                {
                    try
                    {
                        this.func_147202_a(p_147202_1_ + 1, p_147202_2_ + "." + profiler$result.field_76331_c, stringBuilder);
                    }
                    catch (Exception exception)
                    {
                        stringBuilder.append("[[ EXCEPTION ").append(exception).append(" ]]");
                    }
                }
            }
        }
    }

    private static String getWittyComment()
    {
        String[] astring = new String[] {"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

        try
        {
            return astring[(int)(System.nanoTime() % (long)astring.length)];
        }
        catch (Throwable var2)
        {
            return "Witty comment unavailable :(";
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "start", "stop"): null;
    }
}
