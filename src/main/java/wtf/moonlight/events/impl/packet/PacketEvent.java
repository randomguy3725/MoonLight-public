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
package wtf.moonlight.events.impl.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;
import wtf.moonlight.events.impl.CancellableEvent;

@Getter
@AllArgsConstructor
public class PacketEvent extends CancellableEvent {
    @Setter
    private Packet<?> packet;
    private final State state;

    public enum State {
        INCOMING,
        OUTGOING
    }
}
