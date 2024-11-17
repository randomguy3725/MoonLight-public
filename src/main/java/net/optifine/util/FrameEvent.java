package net.optifine.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class FrameEvent
{
    private static final Map<String, Integer> mapEventFrames = new HashMap();

    public static boolean isActive(String name, int frameInterval)
    {
        synchronized (mapEventFrames)
        {
            int i = Minecraft.getMinecraft().entityRenderer.frameCount;
            Integer integer = mapEventFrames.get(name);

            if (integer == null)
            {
                integer = Integer.valueOf(i);
                mapEventFrames.put(name, integer);
            }

            int j = integer.intValue();

            if (i > j && i < j + frameInterval)
            {
                return false;
            }
            else
            {
                mapEventFrames.put(name, Integer.valueOf(i));
                return true;
            }
        }
    }
}
