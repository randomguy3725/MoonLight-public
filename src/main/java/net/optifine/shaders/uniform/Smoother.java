package net.optifine.shaders.uniform;

import java.util.HashMap;
import java.util.Map;
import net.optifine.util.CounterInt;
import net.optifine.util.SmoothFloat;

public class Smoother
{
    private static final Map<Integer, SmoothFloat> mapSmoothValues = new HashMap();
    private static final CounterInt counterIds = new CounterInt(1);

    public static float getSmoothValue(int id, float value, float timeFadeUpSec, float timeFadeDownSec)
    {
        synchronized (mapSmoothValues)
        {
            Integer integer = Integer.valueOf(id);
            SmoothFloat smoothfloat = mapSmoothValues.get(integer);

            if (smoothfloat == null)
            {
                smoothfloat = new SmoothFloat(value, timeFadeUpSec, timeFadeDownSec);
                mapSmoothValues.put(integer, smoothfloat);
            }

            float f = smoothfloat.getSmoothValue(value, timeFadeUpSec, timeFadeDownSec);
            return f;
        }
    }

    public static int getNextId()
    {
        synchronized (counterIds)
        {
            return counterIds.nextValue();
        }
    }

    public static void resetValues()
    {
        synchronized (mapSmoothValues)
        {
            mapSmoothValues.clear();
        }
    }
}
