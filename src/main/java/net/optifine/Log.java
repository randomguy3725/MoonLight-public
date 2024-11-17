package net.optifine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final boolean logDetail = System.getProperty("log.detail", "false").equals("true");

    public static void detail(String s)
    {
        if (logDetail)
        {
            LOGGER.info("[OptiFine] " + s);
        }
    }

    public static void dbg(String s)
    {
        LOGGER.info("[OptiFine] " + s);
    }

    public static void warn(String s)
    {
        LOGGER.warn("[OptiFine] " + s);
    }

    public static void warn(String s, Throwable t)
    {
        LOGGER.warn("[OptiFine] " + s, t);
    }

    public static void error(String s)
    {
        LOGGER.error("[OptiFine] " + s);
    }

    public static void error(String s, Throwable t)
    {
        LOGGER.error("[OptiFine] " + s, t);
    }

    public static void log(String s)
    {
        dbg(s);
    }
}
