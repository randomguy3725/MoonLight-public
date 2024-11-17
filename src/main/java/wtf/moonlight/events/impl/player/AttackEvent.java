package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import wtf.moonlight.events.impl.Event;

@Getter
@AllArgsConstructor
public final class AttackEvent implements Event {
    private final Entity targetEntity;
}
