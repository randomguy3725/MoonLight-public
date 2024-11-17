package net.optifine.util;

public class NumUtils
{
    public static float limit(float val, float min, float max)
    {
        return val < min ? min : (val > max ? max : val);
    }

    public static int mod(int x, int y)
    {
        int i = x % y;

        if (i < 0)
        {
            i += y;
        }

        return i;
    }
}
