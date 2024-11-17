package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.Event;

@AllArgsConstructor
public class LookEvent implements Event {

    public float[] rotation;
}
