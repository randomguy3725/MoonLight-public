package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class Vec3
{
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public Vec3(double x, double y, double z)
    {
        if (x == -0.0D)
        {
            x = 0.0D;
        }

        if (y == -0.0D)
        {
            y = 0.0D;
        }

        if (z == -0.0D)
        {
            z = 0.0D;
        }

        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public Vec3(Vec3i p_i46377_1_)
    {
        this(p_i46377_1_.getX(), p_i46377_1_.getY(), p_i46377_1_.getZ());
    }

    public Vec3 subtractReverse(Vec3 vec)
    {
        return new Vec3(vec.xCoord - this.xCoord, vec.yCoord - this.yCoord, vec.zCoord - this.zCoord);
    }

    public Vec3 normalize()
    {
        double d0 = MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return d0 < 1.0E-4D ? new Vec3(0.0D, 0.0D, 0.0D) : new Vec3(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
    }

    public double dotProduct(Vec3 vec)
    {
        return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
    }

    public Vec3 crossProduct(Vec3 vec)
    {
        return new Vec3(this.yCoord * vec.zCoord - this.zCoord * vec.yCoord, this.zCoord * vec.xCoord - this.xCoord * vec.zCoord, this.xCoord * vec.yCoord - this.yCoord * vec.xCoord);
    }

    public Vec3 subtract(Vec3 vec)
    {
        return this.subtract(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public Vec3 subtract(double x, double y, double z)
    {
        return this.addVector(-x, -y, -z);
    }

    public Vec3 add(Vec3 vec)
    {
        return this.addVector(vec.xCoord, vec.yCoord, vec.zCoord);
    }
    public Vec3 add(BlockPos blockPos)
    {
        return this.addVector(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Vec3 add(double x, double y, double z) {
        return new Vec3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }
    public Vec3 addVector(double x, double y, double z)
    {
        return new Vec3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    public double distanceTo(Vec3 vec)
    {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double distanceTo(final EntityPlayer vec) {
        return distanceTo(new Vec3(vec.posX, vec.posY, vec.posZ));
    }

    public double squareDistanceTo(final EntityPlayer vec) {
        return squareDistanceTo(new Vec3(vec.posX, vec.posY, vec.posZ));
    }

    public double squareDistanceTo(Vec3 vec)
    {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double lengthVector()
    {
        return MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }

    public Vec3 getIntermediateWithXValue(Vec3 vec, double x)
    {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;

        if (d0 * d0 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d3 = (x - this.xCoord) / d0;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3) : null;
        }
    }

    public Vec3 getIntermediateWithYValue(Vec3 vec, double y)
    {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;

        if (d1 * d1 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d3 = (y - this.yCoord) / d1;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3) : null;
        }
    }

    public Vec3 getIntermediateWithZValue(Vec3 vec, double z)
    {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;

        if (d2 * d2 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d3 = (z - this.zCoord) / d2;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3) : null;
        }
    }

    public String toString()
    {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

    public Vec3 rotatePitch(float pitch)
    {
        float f = MathHelper.cos(pitch);
        float f1 = MathHelper.sin(pitch);
        double d0 = this.xCoord;
        double d1 = this.yCoord * (double)f + this.zCoord * (double)f1;
        double d2 = this.zCoord * (double)f - this.yCoord * (double)f1;
        return new Vec3(d0, d1, d2);
    }

    public Vec3 rotateYaw(float yaw)
    {
        float f = MathHelper.cos(yaw);
        float f1 = MathHelper.sin(yaw);
        double d0 = this.xCoord * (double)f + this.zCoord * (double)f1;
        double d1 = this.yCoord;
        double d2 = this.zCoord * (double)f - this.xCoord * (double)f1;
        return new Vec3(d0, d1, d2);
    }

    public Vec3 scale(double factor) {
        return this.mul(factor, factor, factor);
    }

    public Vec3 inverse() {
        return this.scale(-1.0D);
    }

    public Vec3 mul(Vec3 vec) {
        return this.mul(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public Vec3 mul(double factorX, double factorY, double factorZ) {
        return new Vec3(this.xCoord * factorX, this.yCoord * factorY, this.zCoord * factorZ);
    }

    public Vec3 flat() {
        return new Vec3(this.xCoord, 0.0, this.zCoord);
    }
    public Vec3 multiply(double scalar) {
        return new Vec3(this.xCoord * scalar, this.yCoord * scalar, this.zCoord * scalar);
    }
    public double distanceXZTo(Vec3 vec) {
        double d0 = vec.xCoord - this.xCoord;
        double d2 = vec.zCoord - this.zCoord;
        return MathHelper.sqrt_double(d0 * d0 + d2 * d2);
    }

    public double getDistanceAtEyeByVec(Entity self, double x, double y, double z) {
        double d0 = this.xCoord - x;
        double d1 = this.yCoord + (double)(self == null ? 0.0f : self.getEyeHeight()) - y;
        double d2 = this.zCoord - z;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }
}
