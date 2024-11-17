package net.optifine.util;

public class SmoothFloat
{
    private float valueLast;
    private float timeFadeUpSec;
    private float timeFadeDownSec;
    private long timeLastMs;

    public SmoothFloat(float valueLast, float timeFadeSec)
    {
        this(valueLast, timeFadeSec, timeFadeSec);
    }

    public SmoothFloat(float valueLast, float timeFadeUpSec, float timeFadeDownSec)
    {
        this.valueLast = valueLast;
        this.timeFadeUpSec = timeFadeUpSec;
        this.timeFadeDownSec = timeFadeDownSec;
        this.timeLastMs = System.currentTimeMillis();
    }

    public float getValueLast()
    {
        return this.valueLast;
    }

    public float getTimeFadeUpSec()
    {
        return this.timeFadeUpSec;
    }

    public float getTimeFadeDownSec()
    {
        return this.timeFadeDownSec;
    }

    public long getTimeLastMs()
    {
        return this.timeLastMs;
    }

    public float getSmoothValue(float value, float timeFadeUpSec, float timeFadeDownSec)
    {
        this.timeFadeUpSec = timeFadeUpSec;
        this.timeFadeDownSec = timeFadeDownSec;
        return this.getSmoothValue(value);
    }

    public float getSmoothValue(float value)
    {
        long i = System.currentTimeMillis();
        float f = this.valueLast;
        long j = this.timeLastMs;
        float f1 = (float)(i - j) / 1000.0F;
        float f2 = value >= f ? this.timeFadeUpSec : this.timeFadeDownSec;
        float f3 = getSmoothValue(f, value, f1, f2);
        this.valueLast = f3;
        this.timeLastMs = i;
        return f3;
    }

    public static float getSmoothValue(float valPrev, float value, float timeDeltaSec, float timeFadeSec)
    {
        if (timeDeltaSec <= 0.0F)
        {
            return valPrev;
        }
        else
        {
            float f = value - valPrev;
            float f1;

            if (timeFadeSec > 0.0F && timeDeltaSec < timeFadeSec && Math.abs(f) > 1.0E-6F)
            {
                float f2 = timeFadeSec / timeDeltaSec;
                float f3 = 4.61F;
                float f4 = 0.13F;
                float f5 = 10.0F;
                float f6 = f3 - 1.0F / (f4 + f2 / f5);
                float f7 = timeDeltaSec / timeFadeSec * f6;
                f7 = NumUtils.limit(f7, 0.0F, 1.0F);
                f1 = valPrev + f * f7;
            }
            else
            {
                f1 = value;
            }

            return f1;
        }
    }
}
