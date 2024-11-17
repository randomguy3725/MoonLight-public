package net.minecraft.entity.boss;

import net.minecraft.util.IChatComponent;

public interface IBossDisplayData
{
    float getMaxHealth();

    float getHealth();

    IChatComponent getDisplayName();
}
