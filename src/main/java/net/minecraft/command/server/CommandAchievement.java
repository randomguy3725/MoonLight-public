package net.minecraft.command.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;

public class CommandAchievement extends CommandBase
{
    public String getCommandName()
    {
        return "achievement";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.achievement.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.achievement.usage");
        }
        else
        {
            final StatBase statbase = StatList.getOneShotStat(args[1]);

            if (statbase == null && !args[1].equals("*"))
            {
                throw new CommandException("commands.achievement.unknownAchievement", args[1]);
            }
            else
            {
                final EntityPlayerMP entityplayermp = args.length >= 3 ? getPlayer(sender, args[2]) : getCommandSenderAsPlayer(sender);
                boolean flag = args[0].equalsIgnoreCase("give");
                boolean flag1 = args[0].equalsIgnoreCase("take");

                if (flag || flag1)
                {
                    if (statbase == null)
                    {
                        if (flag)
                        {
                            for (Achievement achievement4 : AchievementList.achievementList)
                            {
                                entityplayermp.triggerAchievement(achievement4);
                            }

                            notifyOperators(sender, this, "commands.achievement.give.success.all", entityplayermp.getName());
                        }
                        else if (flag1)
                        {
                            for (Achievement achievement5 : Lists.reverse(AchievementList.achievementList))
                            {
                                entityplayermp.func_175145_a(achievement5);
                            }

                            notifyOperators(sender, this, "commands.achievement.take.success.all", entityplayermp.getName());
                        }
                    }
                    else
                    {
                        if (statbase instanceof Achievement achievement)
                        {

                            if (flag)
                            {
                                if (entityplayermp.getStatFile().hasAchievementUnlocked(achievement))
                                {
                                    throw new CommandException("commands.achievement.alreadyHave", entityplayermp.getName(), statbase.createChatComponent());
                                }

                                List<Achievement> list;

                                for (list = Lists.newArrayList(); achievement.parentAchievement != null && !entityplayermp.getStatFile().hasAchievementUnlocked(achievement.parentAchievement); achievement = achievement.parentAchievement)
                                {
                                    list.add(achievement.parentAchievement);
                                }

                                for (Achievement achievement1 : Lists.reverse(list))
                                {
                                    entityplayermp.triggerAchievement(achievement1);
                                }
                            }
                            else if (flag1)
                            {
                                if (!entityplayermp.getStatFile().hasAchievementUnlocked(achievement))
                                {
                                    throw new CommandException("commands.achievement.dontHave", entityplayermp.getName(), statbase.createChatComponent());
                                }

                                List<Achievement> list1 = Lists.newArrayList(Iterators.filter(AchievementList.achievementList.iterator(), new Predicate<Achievement>()
                                {
                                    public boolean apply(Achievement p_apply_1_)
                                    {
                                        return entityplayermp.getStatFile().hasAchievementUnlocked(p_apply_1_) && p_apply_1_ != statbase;
                                    }
                                }));
                                List<Achievement> list2 = Lists.newArrayList(list1);

                                for (Achievement achievement2 : list1)
                                {
                                    Achievement achievement3 = achievement2;
                                    boolean flag2;

                                    for (flag2 = false; achievement3 != null; achievement3 = achievement3.parentAchievement)
                                    {
                                        if (achievement3 == statbase)
                                        {
                                            flag2 = true;
                                        }
                                    }

                                    if (!flag2)
                                    {
                                        for (achievement3 = achievement2; achievement3 != null; achievement3 = achievement3.parentAchievement)
                                        {
                                            list2.remove(achievement2);
                                        }
                                    }
                                }

                                for (Achievement achievement6 : list2)
                                {
                                    entityplayermp.func_175145_a(achievement6);
                                }
                            }
                        }

                        if (flag)
                        {
                            entityplayermp.triggerAchievement(statbase);
                            notifyOperators(sender, this, "commands.achievement.give.success.one", entityplayermp.getName(), statbase.createChatComponent());
                        }
                        else if (flag1)
                        {
                            entityplayermp.func_175145_a(statbase);
                            notifyOperators(sender, this, "commands.achievement.take.success.one", statbase.createChatComponent(), entityplayermp.getName());
                        }
                    }
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "give", "take");
        }
        else if (args.length != 2)
        {
            return args.length == 3 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
        }
        else
        {
            List<String> list = Lists.newArrayList();

            for (StatBase statbase : StatList.allStats)
            {
                list.add(statbase.statId);
            }

            return getListOfStringsMatchingLastWord(args, list);
        }
    }

    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 2;
    }
}
