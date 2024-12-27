/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import wtf.moonlight.events.impl.CancellableEvent;
import wtf.moonlight.events.impl.Event;

@Getter
@AllArgsConstructor
public final class AttackEvent extends CancellableEvent {
    private final Entity targetEntity;
}
