package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.Event;

@Getter
@Setter
@AllArgsConstructor
public class MoveInputEvent implements Event {
    private float forward;
    private float strafe;
    private boolean jumping;
    private boolean sneaking;
}