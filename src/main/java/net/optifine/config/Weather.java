package net.optifine.config;

import net.minecraft.world.World;

public enum Weather
{
    CLEAR,
    RAIN,
    THUNDER;

    public static Weather getWeather(World world, float partialTicks)
    {
        float f = world.getThunderStrength(partialTicks);

        if (f > 0.5F)
        {
            return THUNDER;
        }
        else
        {
            float f1 = world.getRainStrength(partialTicks);
            return f1 > 0.5F ? RAIN : CLEAR;
        }
    }
}
