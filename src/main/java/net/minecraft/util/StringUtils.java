package net.minecraft.util;

import java.util.regex.Pattern;

public class StringUtils
{
    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String ticksToElapsedTime(int ticks)
    {
        int i = ticks / 20;
        int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    public static String stripControlCodes(String text)
    {
        return patternControlCode.matcher(text).replaceAll("");
    }

    public static boolean isNullOrEmpty(String string)
    {
        return org.apache.commons.lang3.StringUtils.isEmpty(string);
    }
}
