/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.utils.animations;

import net.minecraft.client.Minecraft;

import java.math.BigDecimal;
import java.math.MathContext;

public final class Translate {

    private double x, y;
    private final long lastMS = System.currentTimeMillis();

    public Translate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void translate(final float targetX, final float targetY) {
        this.x = (float)anim(this.x, targetX, 1.0);
        this.y = (float)anim(this.y, targetY, 1.0);
    }

    public void animate(double newX, double newY) {
        x = anim(x, newX, 1.0D);
        y = anim(y, newY, 1.0D);
    }

    public static float smoothAnimation(float ani, float finalState, float speed, float scale) {
        return (float) anim(ani, finalState, (Math.max(10, (Math.abs(ani - finalState)) * speed) * scale));
    }

    public static double anim(final double now, final double desired, final double speed) {
        final double dif = Math.abs(now - desired);
        final int fps = Minecraft.getDebugFPS();
        if (dif > 0.0) {
            double animationSpeed = roundToDecimalPlace(Math.min(10.0, Math.max(0.0625D, 144.0 / fps * dif / 10.0 * speed)), 0.0625D);
            if (dif < animationSpeed) {
                animationSpeed = dif;
            }
            if (now < desired) {
                return now + animationSpeed;
            }
            if (now > desired) {
                return now - animationSpeed;
            }
        }
        return now;
    }

    public static double roundToDecimalPlace(final double value, final double inc) {
        final double halfOfInc = inc / 2.0;
        final double floored = StrictMath.floor(value / inc) * inc;
        if (value >= floored + halfOfInc) {
            return new BigDecimal(StrictMath.ceil(value / inc) * inc, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
        }
        return new BigDecimal(floored, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
    }

    public double getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
