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
package wtf.moonlight.utils.player;

import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;

import static wtf.moonlight.utils.InstanceAccess.mc;

public final class FallDistanceComponent {
    public static float distance;
    private float lastDistance;

    @EventTarget
    private void onMotion(final MotionEvent event) {
        if (event.isPre()) {
            final float fallDistance = mc.thePlayer.fallDistance;
            if (fallDistance == 0.0f) {
                FallDistanceComponent.distance = 0.0f;
            }
            FallDistanceComponent.distance += fallDistance - this.lastDistance;
            this.lastDistance = fallDistance;
        }
    }
}
