package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.StatCollector;

public class Achievement extends StatBase
{
    public final int displayColumn;
    public final int displayRow;
    public final Achievement parentAchievement;
    private final String achievementDescription;
    private IStatStringFormat statStringFormatter;
    public final ItemStack theItemStack;
    private boolean isSpecial;

    public Achievement(String statIdIn, String unlocalizedName, int column, int row, Item itemIn, Achievement parent)
    {
        this(statIdIn, unlocalizedName, column, row, new ItemStack(itemIn), parent);
    }

    public Achievement(String statIdIn, String unlocalizedName, int column, int row, Block blockIn, Achievement parent)
    {
        this(statIdIn, unlocalizedName, column, row, new ItemStack(blockIn), parent);
    }

    public Achievement(String statIdIn, String unlocalizedName, int column, int row, ItemStack stack, Achievement parent)
    {
        super(statIdIn, new ChatComponentTranslation("achievement." + unlocalizedName));
        this.theItemStack = stack;
        this.achievementDescription = "achievement." + unlocalizedName + ".desc";
        this.displayColumn = column;
        this.displayRow = row;

        if (column < AchievementList.minDisplayColumn)
        {
            AchievementList.minDisplayColumn = column;
        }

        if (row < AchievementList.minDisplayRow)
        {
            AchievementList.minDisplayRow = row;
        }

        if (column > AchievementList.maxDisplayColumn)
        {
            AchievementList.maxDisplayColumn = column;
        }

        if (row > AchievementList.maxDisplayRow)
        {
            AchievementList.maxDisplayRow = row;
        }

        this.parentAchievement = parent;
    }

    public Achievement initIndependentStat()
    {
        this.isIndependent = true;
        return this;
    }

    public Achievement setSpecial()
    {
        this.isSpecial = true;
        return this;
    }

    public Achievement registerStat()
    {
        super.registerStat();
        AchievementList.achievementList.add(this);
        return this;
    }

    public boolean isAchievement()
    {
        return true;
    }

    public IChatComponent getStatName()
    {
        IChatComponent ichatcomponent = super.getStatName();
        ichatcomponent.getChatStyle().setColor(this.getSpecial() ? EnumChatFormatting.DARK_PURPLE : EnumChatFormatting.GREEN);
        return ichatcomponent;
    }

    public Achievement func_150953_b(Class <? extends IJsonSerializable > p_150953_1_)
    {
        return (Achievement)super.func_150953_b(p_150953_1_);
    }

    public String getDescription()
    {
        return this.statStringFormatter != null ? this.statStringFormatter.formatString(StatCollector.translateToLocal(this.achievementDescription)) : StatCollector.translateToLocal(this.achievementDescription);
    }

    public Achievement setStatStringFormatter(IStatStringFormat statStringFormatterIn)
    {
        this.statStringFormatter = statStringFormatterIn;
        return this;
    }

    public boolean getSpecial()
    {
        return this.isSpecial;
    }
}
