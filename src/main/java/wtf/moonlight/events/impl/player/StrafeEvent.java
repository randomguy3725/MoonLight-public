package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.CancellableEvent;

@Setter
@Getter
@AllArgsConstructor
public class StrafeEvent extends CancellableEvent {
    private float strafe;
    private float forward;
    private float friction;
    private float yaw;
}