package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandStats extends CommandBase
{
    public String getCommandName()
    {
        return "stats";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.stats.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.stats.usage");
        }
        else
        {
            boolean flag;

            if (args[0].equals("entity"))
            {
                flag = false;
            }
            else
            {
                if (!args[0].equals("block"))
                {
                    throw new WrongUsageException("commands.stats.usage");
                }

                flag = true;
            }

            int i;

            if (flag)
            {
                if (args.length < 5)
                {
                    throw new WrongUsageException("commands.stats.block.usage");
                }

                i = 4;
            }
            else
            {
                if (args.length < 3)
                {
                    throw new WrongUsageException("commands.stats.entity.usage");
                }

                i = 2;
            }

            String s = args[i++];

            if ("set".equals(s))
            {
                if (args.length < i + 3)
                {
                    if (i == 5)
                    {
                        throw new WrongUsageException("commands.stats.block.set.usage");
                    }

                    throw new WrongUsageException("commands.stats.entity.set.usage");
                }
            }
            else
            {
                if (!"clear".equals(s))
                {
                    throw new WrongUsageException("commands.stats.usage");
                }

                if (args.length < i + 1)
                {
                    if (i == 5)
                    {
                        throw new WrongUsageException("commands.stats.block.clear.usage");
                    }

                    throw new WrongUsageException("commands.stats.entity.clear.usage");
                }
            }

            CommandResultStats.Type commandresultstats$type = CommandResultStats.Type.getTypeByName(args[i++]);

            if (commandresultstats$type == null)
            {
                throw new CommandException("commands.stats.failed");
            }
            else
            {
                World world = sender.getEntityWorld();
                CommandResultStats commandresultstats;

                if (flag)
                {
                    BlockPos blockpos = parseBlockPos(sender, args, 1, false);
                    TileEntity tileentity = world.getTileEntity(blockpos);

                    if (tileentity == null)
                    {
                        throw new CommandException("commands.stats.noCompatibleBlock", Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ()));
                    }

                    if (tileentity instanceof TileEntityCommandBlock)
                    {
                        commandresultstats = ((TileEntityCommandBlock)tileentity).getCommandResultStats();
                    }
                    else
                    {
                        if (!(tileentity instanceof TileEntitySign))
                        {
                            throw new CommandException("commands.stats.noCompatibleBlock", Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ()));
                        }

                        commandresultstats = ((TileEntitySign)tileentity).getStats();
                    }
                }
                else
                {
                    Entity entity = getEntity(sender, args[1]);
                    commandresultstats = entity.getCommandStats();
                }

                if ("set".equals(s))
                {
                    String s1 = args[i++];
                    String s2 = args[i];

                    if (s1.length() == 0 || s2.length() == 0)
                    {
                        throw new CommandException("commands.stats.failed");
                    }

                    CommandResultStats.setScoreBoardStat(commandresultstats, commandresultstats$type, s1, s2);
                    notifyOperators(sender, this, "commands.stats.success", commandresultstats$type.getTypeName(), s2, s1);
                }
                else if ("clear".equals(s))
                {
                    CommandResultStats.setScoreBoardStat(commandresultstats, commandresultstats$type, null, null);
                    notifyOperators(sender, this, "commands.stats.cleared", commandresultstats$type.getTypeName());
                }

                if (flag)
                {
                    BlockPos blockpos1 = parseBlockPos(sender, args, 1, false);
                    TileEntity tileentity1 = world.getTileEntity(blockpos1);
                    tileentity1.markDirty();
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "entity", "block"): (args.length == 2 && args[0].equals("entity") ? getListOfStringsMatchingLastWord(args, this.func_175776_d()) : (args.length >= 2 && args.length <= 4 && args[0].equals("block") ? func_175771_a(args, 1, pos) : ((args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block")) ? ((args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block")) ? ((args.length != 6 || !args[0].equals("entity")) && (args.length != 8 || !args[0].equals("block")) ? null : getListOfStringsMatchingLastWord(args, this.func_175777_e())) : getListOfStringsMatchingLastWord(args, CommandResultStats.Type.getTypeNames())) : getListOfStringsMatchingLastWord(args, "set", "clear"))));
    }

    protected String[] func_175776_d()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    protected List<String> func_175777_e()
    {
        Collection<ScoreObjective> collection = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard().getScoreObjectives();
        List<String> list = Lists.newArrayList();

        for (ScoreObjective scoreobjective : collection)
        {
            if (!scoreobjective.getCriteria().isReadOnly())
            {
                list.add(scoreobjective.getName());
            }
        }

        return list;
    }

    public boolean isUsernameIndex(String[] args, int index)
    {
        return args.length > 0 && args[0].equals("entity") && index == 1;
    }
}
