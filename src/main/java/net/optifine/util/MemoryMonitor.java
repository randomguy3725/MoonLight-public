package net.optifine.util;

public class MemoryMonitor
{
    private static long startTimeMs = System.currentTimeMillis();
    private static long startMemory = getMemoryUsed();
    private static long lastTimeMs = startTimeMs;
    private static long lastMemory = startMemory;
    private static boolean gcEvent = false;
    private static int memBytesSec = 0;
    private static final long MB = 1048576L;

    public static void update()
    {
        long i = System.currentTimeMillis();
        long j = getMemoryUsed();
        gcEvent = j < lastMemory;

        if (gcEvent)
        {
            long k = lastTimeMs - startTimeMs;
            long l = lastMemory - startMemory;
            double d0 = (double)k / 1000.0D;
            int i1 = (int)((double)l / d0);

            if (i1 > 0)
            {
                memBytesSec = i1;
            }

            startTimeMs = i;
            startMemory = j;
        }

        lastTimeMs = i;
        lastMemory = j;
    }

    private static long getMemoryUsed()
    {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static long getStartTimeMs()
    {
        return startTimeMs;
    }

    public static long getStartMemoryMb()
    {
        return startMemory / MB;
    }

    public static boolean isGcEvent()
    {
        return gcEvent;
    }

    public static long getAllocationRateMb()
    {
        return (long)memBytesSec / MB;
    }
}
