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
package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.CancellableEvent;

@Setter
@Getter
@AllArgsConstructor
public final class SlowDownEvent extends CancellableEvent {
    private float strafe;
    private float forward;
    private boolean sprinting;
}