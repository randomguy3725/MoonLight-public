package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.util.Vec3;

public class CustomColorFader
{
    private Vec3 color = null;
    private long timeUpdate = System.currentTimeMillis();

    public Vec3 getColor(double x, double y, double z)
    {
        if (this.color == null)
        {
            this.color = new Vec3(x, y, z);
            return this.color;
        }
        else
        {
            long i = System.currentTimeMillis();
            long j = i - this.timeUpdate;

            if (j == 0L)
            {
                return this.color;
            }
            else
            {
                this.timeUpdate = i;

                if (Math.abs(x - this.color.xCoord) < 0.004D && Math.abs(y - this.color.yCoord) < 0.004D && Math.abs(z - this.color.zCoord) < 0.004D)
                {
                    return this.color;
                }
                else
                {
                    double d0 = (double)j * 0.001D;
                    d0 = Config.limit(d0, 0.0D, 1.0D);
                    double d1 = x - this.color.xCoord;
                    double d2 = y - this.color.yCoord;
                    double d3 = z - this.color.zCoord;
                    double d4 = this.color.xCoord + d1 * d0;
                    double d5 = this.color.yCoord + d2 * d0;
                    double d6 = this.color.zCoord + d3 * d0;
                    this.color = new Vec3(d4, d5, d6);
                    return this.color;
                }
            }
        }
    }
}
