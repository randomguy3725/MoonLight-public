package net.minecraft.util;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class ChatComponentScore extends ChatComponentStyle
{
    private final String name;
    private final String objective;
    private String value = "";

    public ChatComponentScore(String nameIn, String objectiveIn)
    {
        this.name = nameIn;
        this.objective = objectiveIn;
    }

    public String getName()
    {
        return this.name;
    }

    public String getObjective()
    {
        return this.objective;
    }

    public void setValue(String valueIn)
    {
        this.value = valueIn;
    }

    public String getUnformattedTextForChat()
    {
        MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (minecraftserver != null && minecraftserver.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value))
        {
            Scoreboard scoreboard = minecraftserver.worldServerForDimension(0).getScoreboard();
            ScoreObjective scoreobjective = scoreboard.getObjective(this.objective);

            if (scoreboard.entityHasObjective(this.name, scoreobjective))
            {
                Score score = scoreboard.getValueFromObjective(this.name, scoreobjective);
                this.setValue(String.format("%d", Integer.valueOf(score.getScorePoints())));
            }
            else
            {
                this.value = "";
            }
        }

        return this.value;
    }

    public ChatComponentScore createCopy()
    {
        ChatComponentScore chatcomponentscore = new ChatComponentScore(this.name, this.objective);
        chatcomponentscore.setValue(this.value);
        chatcomponentscore.setChatStyle(this.getChatStyle().createShallowCopy());

        for (IChatComponent ichatcomponent : this.getSiblings())
        {
            chatcomponentscore.appendSibling(ichatcomponent.createCopy());
        }

        return chatcomponentscore;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatComponentScore chatcomponentscore))
        {
            return false;
        }
        else
        {
            return this.name.equals(chatcomponentscore.name) && this.objective.equals(chatcomponentscore.objective) && super.equals(p_equals_1_);
        }
    }

    public String toString()
    {
        return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }
}
