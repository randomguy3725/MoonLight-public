package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class MoveEvent extends CancellableEvent {
    public double x, y, z;
}