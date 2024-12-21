package wtf.moonlight.utils.math;

import net.minecraft.util.*;

import java.security.SecureRandom;
import java.util.Random;

import static wtf.moonlight.utils.InstanceAccess.mc;

public class MathUtils {
    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

    public static double incValue(double val, double inc) {
        double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

    public static double interpolate(double old,
                                     double now,
                                     float partialTicks) {
        return old + (now - old) * partialTicks;
    }

    public static double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float interpolate(float old,
                                    float now,
                                    float partialTicks) {

        return old + (now - old) * partialTicks;
    }

    public static Vec3 interpolate(Vec3 end, Vec3 start, float multiple) {
        return new Vec3(
                (float) interpolate(end.xCoord, start.xCoord, multiple),
                (float) interpolate(end.yCoord, start.yCoord, multiple),
                (float) interpolate(end.zCoord, start.zCoord, multiple));
    }
    public static double interpolate(double old, double now) {
        return interpolate(old,now,mc.timer.renderPartialTicks);
    }

    public static float nextSecureFloat(final double origin, final double bound) {
        if (origin == bound) {
            return (float) origin;
        }
        final SecureRandom secureRandom = new SecureRandom();
        final float difference = (float) (bound - origin);
        return (float) (origin + secureRandom.nextFloat() * difference);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = Math.PI;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public static double lerp(double pct, double start, double end) {
        return start + pct * (end - start);
    }

    public static float lerp(float min, float max, float delta) {
        return min + (max - min) * delta;
    }

    public static int nextInt(int min, int max) {
        if (min == max || max - min <= 0D)
            return min;

        return (int) (min + ((max - min) * Math.random()));
    }

    public static double nextDouble(double min, double max) {
        if (min == max || max - min <= 0D)
            return min;

        return min + ((max - min) * Math.random());
    }

    public static double interporate(double p_219803_0_, double p_219803_2_, double p_219803_4_) {
        return p_219803_2_ + p_219803_0_ * (p_219803_4_ - p_219803_2_);
    }

    public static float normalize(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    public static float nextFloat(final float startInclusive, final float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0F)
            return startInclusive;

        return (float) (startInclusive + ((endInclusive - startInclusive) * Math.random()));
    }

    public static int randomizeInt(double min, double max) {
        return (int) randomizeDouble(min, max);
    }

    public static double randomizeDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static boolean inBetween(double min, double max, double value) {
        return value >= min && value <= max;
    }

    public static double wrappedDifference(double number1, double number2) {
        return Math.min(Math.abs(number1 - number2), Math.min(Math.abs(number1 - 360) - Math.abs(number2 - 0), Math.abs(number2 - 360) - Math.abs(number1 - 0)));
    }
    public static float getAdvancedRandom(float min, float max) {
        SecureRandom random = new SecureRandom();

        long finalSeed = System.nanoTime();

        for (int i = 0; i < 3; ++i) {
            long seed = (long) (Math.random() * 1_000_000_000);

            seed ^= (seed << 13);
            seed ^= (seed >>> 17);
            seed ^= (seed << 15);

            finalSeed += seed;
        }

        random.setSeed(finalSeed);

        return random.nextFloat() * (max - min) + min;
    }

    public static Vec3 closestPointOnFace(AxisAlignedBB aabb, EnumFacing face, double x, double y, double z) {
        double closestX, closestY, closestZ;

        switch (face) {
            case DOWN, UP -> {
                closestX = Math.max(aabb.minX, Math.min(x, aabb.maxX));
                closestY = face == EnumFacing.DOWN ? aabb.minY : aabb.maxY;
                closestZ = Math.max(aabb.minZ, Math.min(z, aabb.maxZ));
            }
            case NORTH, SOUTH -> {
                closestX = Math.max(aabb.minX, Math.min(x, aabb.maxX));
                closestY = Math.max(aabb.minY, Math.min(y, aabb.maxY));
                closestZ = face == EnumFacing.NORTH ? aabb.minZ : aabb.maxZ;
            }
            case WEST, EAST -> {
                closestX = face == EnumFacing.WEST ? aabb.minX : aabb.maxX;
                closestY = Math.max(aabb.minY, Math.min(y, aabb.maxY));
                closestZ = Math.max(aabb.minZ, Math.min(z, aabb.maxZ));
            }
            default -> throw new IllegalArgumentException("Invalid face: " + face);
        }

        return new Vec3(closestX, closestY, closestZ);
    }

    public static Vec3 closestPointOnFace(AxisAlignedBB aabb, EnumFacing face, Vec3 vec) {
        return closestPointOnFace(aabb, face, vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static double randomSin() {
        return Math.sin(nextDouble(0.0, Math.PI * 2));
    }
}
