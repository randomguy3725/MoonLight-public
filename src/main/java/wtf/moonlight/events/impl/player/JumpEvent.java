package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.CancellableEvent;

@Setter
@Getter
@AllArgsConstructor
public class JumpEvent extends CancellableEvent {
    private float motionY;
    private float yaw;
}
