package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint
{
    public final int xCoord;
    public final int yCoord;
    public final int zCoord;
    private final int hash;
    int index = -1;
    float totalPathDistance;
    float distanceToNext;
    float distanceToTarget;
    PathPoint previous;
    public boolean visited;

    public PathPoint(int x, int y, int z)
    {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.hash = makeHash(x, y, z);
    }

    public static int makeHash(int x, int y, int z)
    {
        return y & 255 | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
    }

    public float distanceTo(PathPoint pathpointIn)
    {
        float f = (float)(pathpointIn.xCoord - this.xCoord);
        float f1 = (float)(pathpointIn.yCoord - this.yCoord);
        float f2 = (float)(pathpointIn.zCoord - this.zCoord);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    public float distanceToSquared(PathPoint pathpointIn)
    {
        float f = (float)(pathpointIn.xCoord - this.xCoord);
        float f1 = (float)(pathpointIn.yCoord - this.yCoord);
        float f2 = (float)(pathpointIn.zCoord - this.zCoord);
        return f * f + f1 * f1 + f2 * f2;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (!(p_equals_1_ instanceof PathPoint pathpoint))
        {
            return false;
        }
        else
        {
            return this.hash == pathpoint.hash && this.xCoord == pathpoint.xCoord && this.yCoord == pathpoint.yCoord && this.zCoord == pathpoint.zCoord;
        }
    }

    public int hashCode()
    {
        return this.hash;
    }

    public boolean isAssigned()
    {
        return this.index >= 0;
    }

    public String toString()
    {
        return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}
